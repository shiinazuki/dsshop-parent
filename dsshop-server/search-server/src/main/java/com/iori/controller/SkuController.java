package com.iori.controller;

import com.iori.repository.SkuRepository;
import com.iori.utils.ReturnUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/sku")
public class SkuController {

    @Autowired
    private SkuRepository skuRepository;

    @PostMapping("/query")
    public ReturnUtil queryAll(@RequestBody Map<String,String> map) throws IOException {
        return skuRepository.search(map);
    }

}
