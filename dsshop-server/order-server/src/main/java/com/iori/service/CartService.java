package com.iori.service;

import com.iori.bean.OrderItem;

import java.util.List;

public interface CartService {

    /**
     * 将购物车信息添加到redis
     * @param skuId
     * @param num
     * @param username
     * @return
     */
    boolean add(String skuId,Integer num,String username);

    /**
     * 获取redis中购物车的信息
     * @param username
     * @return
     */
    List<OrderItem> list(String username);

    /**
     * 移除购物车中的数据
     * @return
     */
    boolean remove(String[] skuIds,String username);

    /**
     * 查询购物车中的数据
     * @return
     */
    List<OrderItem> select(String[] skuIds,String username);


}
