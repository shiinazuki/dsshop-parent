package com.iori;

import com.iori.bean.Sku;
import com.iori.repository.SkuRepository;
import com.iori.utils.ReturnUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@SpringBootApplication
public class MySearchApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext run = SpringApplication.run(MySearchApplication.class, args);
        SkuRepository bean = run.getBean(SkuRepository.class);
        //bean.create();
        //bean.importData();

        Map<String, String> map = new HashMap<>();
        map.put("index", "1");
        map.put("size", "50");
        map.put("keyword", "小米");
        //map.put("brand","小米,华为");
        //map.put("cate","拉杆箱,老花镜");
        //map.put("spec_颜色","黑色");
        //map.put("spec_版本","6GB+64GB");

        map.put("price", "50000-*");
        map.put("sortField","price");
        map.put("sortRule","DESC");


        ReturnUtil search = bean.search(map);
        Long count = search.getCount();
        List<String> brandList = search.getBrandList();
        List<String> cateList = search.getCateList();
        List<Sku> skus = search.getSkus();
        Map<String, Set<String>> specMap = search.getSpecMap();

        System.out.println(count);
        System.out.println("=======================");
        for (String s : brandList) {
            System.out.println(s);
        }
        System.out.println("=======================");
        for (String s : cateList) {
            System.out.println(s);
        }
        System.out.println("=======================");
        for (Sku sku : skus) {
            System.out.println(sku);
        }
        System.out.println("=======================");
        Set<Map.Entry<String, Set<String>>> entries = specMap.entrySet();
        for (Map.Entry<String, Set<String>> entry : entries) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }

    }

}
