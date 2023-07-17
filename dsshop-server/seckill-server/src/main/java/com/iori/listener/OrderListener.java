package com.iori.listener;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.iori.bean.SecKillGoods;
import com.iori.bean.SecKillOrder;
import com.iori.service.SecKillGoodsService;
import com.iori.util.SecKillStatus;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;


@Component
public class OrderListener {


    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SecKillGoodsService secKillGoodsService;

    /**
     * 监听twoQueue队列
     *
     * @param msg
     */
    @RabbitListener(queues = "twoQueue")
    public void rollbackOrder(String msg) {
        //将json转为对象
        SecKillStatus secKillStatus = JSON.parseObject(msg, SecKillStatus.class);
        Long id = secKillStatus.getGoodsId();
        //根据时间段和商品id查询商品
        SecKillGoods secKillGoods = (SecKillGoods) redisTemplate.boundHashOps("secKill:" + secKillStatus.getTime())
                .get(id + "");

        //判断商品对象为不为 null 如果不为 null 直接修改 为 null 则重新创建键
        if (ObjectUtils.isEmpty(secKillGoods)) {
            //从数据库查到信息
            SecKillGoods byId = secKillGoodsService.getById(id);
            //个数加1
            byId.setStockCount(byId.getStockCount() + 1);
            //更新数据库
            secKillGoodsService.updateById(byId);
            //在重新查一遍
            byId = secKillGoodsService.getById(id);

            //把数据存到redis
            redisTemplate.boundHashOps("seckill:" + secKillStatus.getTime()).put(id + "", byId);
            //在redis 创建数量队列 大小为商品库存数
            Integer num = byId.getStockCount();
            Long[] ids = this.ids(num, id);
            redisTemplate.boundListOps("secKillNumQueue:" + id).leftPushAll(ids);


        } else {
            //商品库存 +1
            secKillGoods.setStockCount(secKillGoods.getStockCount() + 1);
            //重新把信息更新到redis中
            redisTemplate.boundHashOps("secKill:" + secKillStatus.getTime()).put(secKillStatus.getGoodsId(), secKillGoods);
            redisTemplate.boundListOps("secKillNumQueue:" + secKillStatus.getGoodsId()).leftPush(secKillStatus.getGoodsId());


        }

        //改变订单状态
        SecKillOrder secKillOrder = (SecKillOrder) redisTemplate.boundHashOps("seckillOrder").get(secKillStatus.getUsername());
        secKillOrder.setStatus("0");
        redisTemplate.boundHashOps("seckillOrder").put(secKillStatus.getUsername(), secKillOrder);
        //删除点击
        redisTemplate.boundHashOps("userStockCount").delete(secKillStatus.getUsername() + ":" + id);
        //用户状态
        secKillStatus.setStatus(0);
        redisTemplate.boundHashOps("userSecKillStatus").put(secKillStatus.getUsername(), secKillStatus);

        System.out.println("数据回滚完成");

    }

    /**
     * 存放商品数量的方法
     *
     * @param len
     * @param id
     * @return
     */
    public Long[] ids(Integer len, Long id) {
        Long[] ids = new Long[len];
        for (Integer i = 0; i < len; i++) {
            ids[i] = id;
        }
        return ids;
    }

}
