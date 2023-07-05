package com.iori.controller;

import com.iori.bean.OrderItem;
import com.iori.client.AddressFeignClient;
import com.iori.config.TokenDecode;
import com.iori.dto.AddressDTO;
import com.iori.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {


    @Autowired
    private CartService cartService;
    @Autowired
    private AddressFeignClient addressFeignClient;

    /**
     * 根据传来的参数 生成订单信息 存放带redis中
     * @param skuId
     * @param num
     * @return
     */
    @PostMapping("/addCart")
    public Boolean cart(@RequestParam("skuId") String skuId,@RequestParam("num") Integer num) {
        //获取到用户名
        Map<String, String> userInfo = TokenDecode.getUserInfo();
        String username = userInfo.get("username");
        return cartService.add(skuId,num,username);
    }

    /**
     * 查看全部购物车信息方法
     * @return
     */
    @GetMapping("/list")
    public List<OrderItem> list() {
        //获取用户名
        Map<String, String> userInfo = TokenDecode.getUserInfo();
        String username = userInfo.get("username");
        return cartService.list(username);
    }

    /**
     * 删除购物车信息
     * @param skuIds
     * @return
     */
    @DeleteMapping("/remove")
    public boolean remove(@RequestParam("skuIds") String[] skuIds) {
        //获取用户名
        Map<String, String> userInfo = TokenDecode.getUserInfo();
        String username = userInfo.get("username");
        return cartService.remove(skuIds,username);
    }


    /**
     * 查询购物车信息 和地址
     * @param skuIds
     * @return
     */
    @GetMapping("/select")
    public Map<String,Object> select(@RequestParam("skuIds") String[] skuIds) {

        Map<String,Object> map = new HashMap<>();

        //获取用户名
        Map<String, String> userInfo = TokenDecode.getUserInfo();
        String username = userInfo.get("username");

        List<AddressDTO> address = addressFeignClient.query(username);
        List<OrderItem> orderItemList = cartService.select(skuIds, username);


        map.put("address",address);
        map.put("orderItemList",orderItemList);
        return map;

    }


}
