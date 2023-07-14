package com.iori.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iori.bean.SecKillGoods;

import java.util.List;

public interface SecKillGoodsService extends IService<SecKillGoods> {

    /**
     * 根据时间查询商品
     * @param time
     * @return
     */
     List<SecKillGoods> list(String time);

    /**
     * 根据时间和编号查询商品
     * @param time
     * @param id
     * @return
     */
    SecKillGoods one(String time,String id);

    /**
     *
     * @param time
     * @param id
     * @param username
     * @return
     */
    String createOrder(String time, String id, String username);

}
