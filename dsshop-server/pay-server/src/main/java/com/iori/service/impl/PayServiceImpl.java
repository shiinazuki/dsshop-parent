package com.iori.service.impl;

import com.github.wxpay.sdk.WXPayUtil;
import com.iori.service.PayService;
import com.iori.util.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PayServiceImpl implements PayService {

    @Value("${weixin.appid}")
    private String appid;
    @Value("${weixin.partner}")
    private String partner;
    @Value("${weixin.partnerkey}")
    private String partnerkey;
    @Value("${weixin.notifyurl}")
    private String notifyurl;

    @Override
    public Map<String, String> create(String money, String orderId) {

        //根据微信本地支付封装参数
        Map<String,String> map = new HashMap<>();
        map.put("appid",appid);
        map.put("mch_id",partner);
        map.put("nonce_str", WXPayUtil.generateNonceStr());
        map.put("body","测试");
        map.put("out_trade_no",orderId);
        map.put("total_fee",money);
        map.put("notify_url",notifyurl);
        map.put("trade_type","NATIVE");
        try {
            String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
            String xml = WXPayUtil.generateSignedXml(map, partnerkey);
            //使用 HttpClient 发送请求
            HttpClient httpClient = new HttpClient(url);
            //设置xml参数
            httpClient.setXmlParam(xml);
            //设置为 post 请求
            httpClient.post();
            httpClient.setHttps(true);
            //获取连接 拿到返回参数
            String value = httpClient.getContent();
            return WXPayUtil.xmlToMap(value);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 主动调用查询状态
     * @param orderId
     * @return
     */
    @Override
    public Map<String, String> query(String orderId) {
        //根据微信本地支付封装参数
        Map<String,String> map = new HashMap<>();
        map.put("appid",appid);
        map.put("mch_id",partner);
        map.put("out_trade_no",orderId);
        map.put("nonce_str", WXPayUtil.generateNonceStr());
        try {
            String url = "https://api.mch.weixin.qq.com/pay/orderquery";
            String xml = WXPayUtil.generateSignedXml(map, partnerkey);
            //使用 HttpClient 发送请求
            HttpClient httpClient = new HttpClient(url);
            //设置xml参数
            httpClient.setXmlParam(xml);
            //设置为 post 请求
            httpClient.post();
            httpClient.setHttps(true);
            //获取连接 拿到返回参数
            String value = httpClient.getContent();
            return WXPayUtil.xmlToMap(value);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
