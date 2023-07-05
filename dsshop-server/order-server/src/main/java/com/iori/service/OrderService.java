package com.iori.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iori.bean.Order;
import com.iori.vo.OrderVo;

public interface OrderService extends IService<Order> {

    boolean create(OrderVo orderVo,String username);

}
