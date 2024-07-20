package com.iori.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iori.bean.SecKillGoods;
import com.iori.job.MultiThreadingCreateOrder;
import com.iori.mapper.SecKillGoodsMapper;
import com.iori.service.SecKillGoodsService;
import com.iori.util.SecKillStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SecKillGoodsServiceImpl extends ServiceImpl<SecKillGoodsMapper, SecKillGoods> implements SecKillGoodsService {

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 从 redis 中取出 当前时间段的所有数据
     *
     * @param time
     * @return
     */
    @Override
    public List<SecKillGoods> list(String time) {
        return redisTemplate.boundHashOps("seckill:" + time).values();
    }

    /**
     * 从redis 中 根据时间段 和 用户id 查询对应数据
     *
     * @param time
     * @param id
     * @return
     */
    @Override
    public SecKillGoods one(String time, String id) {
        return (SecKillGoods) redisTemplate.boundHashOps("seckill:" + time).get(id);
    }


    @Autowired
    private MultiThreadingCreateOrder multiThreadingCreateOrder;

    /**
     * 创建订单方法
     */
    @Override
    public String createOrder(String time, String id, String username) {

        //防止用户重复提交 设置一个递增值
        Long userStockCount = redisTemplate.boundHashOps("userStockCount")
                .increment(username + ":" + id, 1);
        //如果这个值大于1 直接return
        if (userStockCount > 1) {
            return "2";
        }


        //将传传来的数据封装成对象
        SecKillStatus secKillStatus = new SecKillStatus(username,new Date(),1,Long.parseLong(id),time);
        //使用 List类型 调用 leftPush进行存储用户信息队列 保证用户的公平性
        //因为队列是先进先出 把进来的用户放入到队列中 保证公平
        redisTemplate.boundListOps("userSecKillQueue").leftPush(secKillStatus);

        //使用 hash 类型 将用户状态存储到redis中 key为 username value 为  用户信息对象
        redisTemplate.boundHashOps("userSecKillStatus").put(username,secKillStatus);

        //异步调用方法
        multiThreadingCreateOrder.asyncCreateOrder();
        return "1";
    }
}
