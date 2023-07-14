package com.iori.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iori.bean.SecKillGoods;
import com.iori.bean.SecKillOrder;
import com.iori.job.MultiThreadingCreateOrder;
import com.iori.mapper.SecKillGoodsMapper;
import com.iori.service.SecKillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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

        //异步调用方法
        multiThreadingCreateOrder.asyncCreateOrder();
        return "1";
    }
}
