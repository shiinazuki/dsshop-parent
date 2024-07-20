package com.iori.job;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.iori.bean.SecKillGoods;
import com.iori.bean.SecKillOrder;
import com.iori.config.MQConfig;
import com.iori.service.SecKillGoodsService;
import com.iori.util.SecKillStatus;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class MultiThreadingCreateOrder {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SecKillGoodsService secKillGoodsService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Async
    public void asyncCreateOrder() {

        //从redis中获取队列中的用户数据
        SecKillStatus secKillStatus = (SecKillStatus) redisTemplate.boundListOps("userSecKillQueue").rightPop();

        String time = secKillStatus.getTime();
        String id = secKillStatus.getGoodsId().toString();
        String username = secKillStatus.getUsername();


        //取出redis中的用户状态信息
        SecKillStatus userStatus = (SecKillStatus) redisTemplate.boundHashOps("userSecKillStatus").get(username);

        //获取商品数量队列
        Object queueValue = redisTemplate.boundListOps("secKillNumQueue:" + id).rightPop();
        //如果这个值为null 则return
        if (ObjectUtils.isEmpty(queueValue)) {
            System.out.println("商品售完,没有库存");
            //修改状态为 失败
            userStatus.setStatus(4);
            //重新存储下
            redisTemplate.boundHashOps("userSecKillStatus").put(username, userStatus);
            return;
        }

        //加锁
        RLock fairLock = redissonClient.getFairLock(id);
        fairLock.lock();
        //根据商品编号查询商品信息 再次验证
        SecKillGoods secKillGoods = (SecKillGoods) redisTemplate.boundHashOps("seckill:" + time).get(id);
        //判断商品库存还有没有
        if (ObjectUtils.isEmpty(secKillGoods) || secKillGoods.getStockCount() <= 0) {
            System.out.println("商品售完,没有库存");
            //修改状态为 失败
            userStatus.setStatus(4);
            //重新存储下
            redisTemplate.boundHashOps("userSecKillStatus").put(username, userStatus);
            return;
        }
        //封装订单对象
        SecKillOrder secKillOrder = new SecKillOrder();
        secKillOrder.setId(IdWorker.getId());
        secKillOrder.setMoney(secKillGoods.getPrice());
        secKillOrder.setUserId(username);
        secKillOrder.setStatus("0");
        //修改状态为 待支付
        userStatus.setStatus(2);
        //存放到redis中 键为username 值为订单对象
        redisTemplate.boundHashOps("seckillOrder").put(username, secKillOrder);
        //修改用户订单状态
        redisTemplate.boundHashOps("userSecKillStatus").put(username, userStatus);

        //让库存减1
        secKillGoods.setStockCount(secKillGoods.getStockCount() - 1);
        //如果库存减后大于0 更新redis的库存
        if (secKillGoods.getStockCount() > 0) {
            redisTemplate.boundHashOps("seckill:" + time).put(id, secKillGoods);
        } else {
            //如果为0就删除 记录
            redisTemplate.boundHashOps("seckill:" + time).delete(id);
            //更新库存
            UpdateWrapper<SecKillGoods> updateWrapper = new UpdateWrapper<>();
            //设置条件
            updateWrapper.lambda().set(SecKillGoods::getStockCount, 0).eq(SecKillGoods::getId, id);
            secKillGoodsService.update(updateWrapper);

        }

        //把订单发送到消息队列
        rabbitTemplate.convertAndSend(MQConfig.ONE_EXCHANGE, MQConfig.ONE_ROUTING, JSON.toJSONString(userStatus), message -> {
            message.getMessageProperties().setContentEncoding("UTF-8");
            message.getMessageProperties().setExpiration("15000");
            return message;
        });

        //释放锁
        fairLock.unlock();

    }

}
