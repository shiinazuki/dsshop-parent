package com.iori.controller;

import com.iori.config.TokenDecode;
import com.iori.service.OrderService;
import com.iori.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    private boolean createOrder(@RequestBody OrderVo orderVo) {
        //获取用户名
        Map<String, String> userInfo = TokenDecode.getUserInfo();
        String username = userInfo.get("username");
        orderService.create(orderVo,username);

        return true;
    }

}
