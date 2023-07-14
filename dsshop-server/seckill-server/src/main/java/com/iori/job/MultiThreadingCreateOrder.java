package com.iori.job;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.iori.bean.SecKillGoods;
import com.iori.bean.SecKillOrder;
import com.iori.service.SecKillGoodsService;
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

    @Async
    public void asyncCreateOrder() {
        synchronized (MultiThreadingCreateOrder.class) {

            String time = "2023071416";
            String id = "2";
            String username = "iori";

            //查询redis中的商品数据
            SecKillGoods secKillGoods = (SecKillGoods) redisTemplate.boundHashOps("seckill:" + time).get(id);
            //判断商品库存还有没有
            if (ObjectUtils.isEmpty(secKillGoods) || secKillGoods.getStockCount() <= 0) {
                System.out.println("商品售完,没有库存");
                return;
            }
            //封装订单对象
            SecKillOrder secKillOrder = new SecKillOrder();
            secKillOrder.setId(IdWorker.getId());
            secKillOrder.setMoney(secKillGoods.getPrice());
            secKillOrder.setUserId(username);
            secKillOrder.setStatus("0");
            //存放到redis中 键为username 值为订单对象
            redisTemplate.boundHashOps("seckillOrder").put(username, secKillOrder);
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

        }
    }

}
