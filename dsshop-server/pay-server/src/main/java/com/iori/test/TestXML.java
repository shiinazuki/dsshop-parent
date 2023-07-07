package com.iori.test;

import com.github.wxpay.sdk.WXPayUtil;

import java.util.HashMap;
import java.util.Map;

public class TestXML {
    public static void main(String[] args) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("appid", "wx2421b1c4370ec43b");
        map.put("attach", "测试");

        //xml转map
        //String xml = WXPayUtil.mapToXml(map);
        String xml = WXPayUtil.generateSignedXml(map, "xxxx");
        System.out.println(xml);
        System.out.println("===============");
        //map转xml
        map = WXPayUtil.xmlToMap(xml);
        System.out.println(map);


    }
}
