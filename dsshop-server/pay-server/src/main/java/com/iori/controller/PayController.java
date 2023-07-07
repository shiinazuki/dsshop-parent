package com.iori.controller;

import com.github.wxpay.sdk.WXPayUtil;
import com.iori.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private PayService payService;

    @GetMapping("/hello")
    public String hello(String name) {
        return "hello" + name;
    }


    @GetMapping("/create")
    public Map<String, String> create(@RequestParam("money") String money, @RequestParam("orderId") String orderId) {
        return payService.create(money, orderId);
    }

    /**
     * 微信回调的方法
     *
     * @param request
     * @return
     */
    @RequestMapping("/notfly")
    public String notfly(HttpServletRequest request) {
        ServletInputStream inputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;

        try {
            inputStream = request.getInputStream();
            byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }

            String content = new String(byteArrayOutputStream.toByteArray(), "UTF-8");

            Map<String, String> result = new HashMap<>();
            result.put("return_code", "SUCCESS");
            result.put("return_msg", "OK");
            return WXPayUtil.mapToXml(result);

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @GetMapping("/query")
    public Map<String, String> query(@RequestParam("orderId") String orderId) {
        return payService.query(orderId);
    }


}
