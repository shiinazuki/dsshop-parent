package com.iori.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "item-server")
public interface ItemFeignClient {

    @GetMapping("/item/create/{skuId}")
    String create(@PathVariable("skuId") String skuId);

}
