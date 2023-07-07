package com.iori.service.impl;

import com.alibaba.fastjson.JSON;
import com.iori.client.SkuFeignClient;
import com.iori.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

@Service
public class PageServiceImpl implements PageService {

    @Autowired
    private SkuFeignClient skuFeignClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Value("${pagePath}")
    private String pagePath;

    /**
     * 生成html文件
     *
     * @param sku
     */
    @Override
    public void createHtml(String sku) {

        Context context = new Context();
        context.setVariables(buildDate(sku));

        File file = new File(pagePath);
        if (!file.exists()) {
            file.mkdir();
        }

        File createFile = new File(pagePath, sku + ".html");

        try (PrintWriter writer = new PrintWriter(createFile, "UTF-8")) {
            templateEngine.process("item", context, writer);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }


    }

    private Map<String, Object> buildDate(String sku) {
        Map<String, Object> map = skuFeignClient.info(sku);
        Map<String, Object> skuMap = (Map<String, Object>) map.get("sku");
        String[] imageList = skuMap.get("images").toString().split(",");
        map.put("imageList",imageList);

        Map<String, Object> spuMap = ( Map<String, Object>)map.get("spu");
        Map<String, Object> specItems = JSON.parseObject(spuMap.get("specItems").toString(), Map.class);


        map.put("specItems",specItems);

        return map;
    }

}
