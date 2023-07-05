package com.iori.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value = "goods-server")
public interface SkuFeignClient {

    @GetMapping("/goods-sku/info/{skuId}")
    Map<String, Object> info(@PathVariable("skuId") String skuId);

    @PutMapping("/goods-sku/updateNum")
    String updateNum(@RequestParam("num") Integer num,
                     @RequestParam("id") String id);
}
