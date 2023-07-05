package com.iori.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(value = "goods-server")
public interface SkuFeignClient {

    @GetMapping("/goods-sku/info/{skuId}")
     Map<String,Object> info(@PathVariable("skuId") String skuId);
    System.out.println("你好git");
}
