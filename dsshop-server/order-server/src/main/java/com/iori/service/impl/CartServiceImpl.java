package com.iori.service.impl;

import com.iori.bean.OrderItem;
import com.iori.client.SkuFeignClient;
import com.iori.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private SkuFeignClient skuFeignClient;
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 把购物车信息 存到redis中
     *
     * @param skuId
     * @param num
     * @param username
     * @return
     */
    @Override
    public boolean add(String skuId, Integer num, String username) {

        //根据skuId拿到全部信息
        Map<String, Object> infoMap = skuFeignClient.info(skuId);
        Map<String, Object> skuMap = (Map<String, Object>) infoMap.get("sku");
        Map<String, Object> spuMap = (Map<String, Object>) infoMap.get("spu");
        //先判断redis里有没有数据
        OrderItem orderItem = (OrderItem) redisTemplate.boundHashOps("cart:" + username).get(skuId);
        //如果没有数据 则封装数据 加入到redis
        if (ObjectUtils.isEmpty(orderItem)) {
            //创建orderItem对象 用来封装数据
            orderItem = new OrderItem();
            orderItem.setCategoryId1(Integer.parseInt(spuMap.get("category1Id").toString()));
            orderItem.setCategoryId2(Integer.parseInt(spuMap.get("category2Id").toString()));
            orderItem.setCategoryId3(Integer.parseInt(spuMap.get("category3Id").toString()));
            orderItem.setSpuId(spuMap.get("id").toString());
            orderItem.setSkuId(skuId);
            orderItem.setName(skuMap.get("name").toString());
            orderItem.setPrice(Integer.parseInt(skuMap.get("price").toString()));
            orderItem.setNum(num);
            orderItem.setMoney(orderItem.getPrice() * num);
            orderItem.setImage(skuMap.get("image").toString());
            orderItem.setWeight(Integer.parseInt(skuMap.get("weight").toString()));
        } else {
            //如果有数据 则重新算下价格和数量
            orderItem.setNum(orderItem.getNum() + num);
            orderItem.setPrice(orderItem.getNum() * orderItem.getPrice());
        }
        //把封装好的数据存入到redis中
        redisTemplate.boundHashOps("cart:" + username).put(skuId, orderItem);

        return true;
    }

    /**
     * 根据用户名查看购物车信息
     *
     * @param username
     * @return
     */
    @Override
    public List<OrderItem> list(String username) {
        //调用redisTemplate 的boundHashOps方法 根据这个对象调用values方法 拿到全部值
        List values = redisTemplate.boundHashOps("cart:" + username).values();
        return values;
    }

    /**
     * 移除购物车信息
     *
     * @param skuIds
     * @param username
     * @return
     */
    @Override
    public boolean remove(String[] skuIds, String username) {

        redisTemplate.boundHashOps("cart:" + username).delete(skuIds);
        return true;
    }

    /**
     * 查询购物车信息
     *
     * @param skuIds
     * @param username
     * @return
     */
    @Override
    public List<OrderItem> select(String[] skuIds, String username) {
        List<OrderItem> orderItemList = new ArrayList<>();
        for (String skuId : skuIds) {
            OrderItem orderItem = (OrderItem) redisTemplate.boundHashOps("cart:" + username).get(skuId);
            orderItemList.add(orderItem);
        }
      return orderItemList;
    }
}
