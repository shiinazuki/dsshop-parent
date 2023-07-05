package com.iori.controller;

import com.iori.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/item")
public class ItemWebController {

    @Autowired
    private PageService pageService;

    @GetMapping("/create/{skuId}")
    public String create(@PathVariable("skuId") String skuId) {
        pageService.createHtml(skuId);
        return "创建html页面成功";

    }

}
