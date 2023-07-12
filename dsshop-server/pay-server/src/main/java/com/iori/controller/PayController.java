package com.iori.controller;

import com.alibaba.fastjson.JSON;
import com.github.wxpay.sdk.WXPayUtil;
import com.iori.service.PayService;
import com.iori.util.MyConnectionFactory;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private PayService payService;
    @Autowired
    private RabbitTemplate rabbitTemplate;


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

            //接收返回的xml消息
            String content = new String(byteArrayOutputStream.toByteArray(), "UTF-8");
            //将xml转为map集合
            Map<String, String> map = WXPayUtil.xmlToMap(content);
            //将map转为xml
            String json = JSON.toJSONString(map);
            //调用sendMQ方法 把数据传入
            //this.sendMQ(json);

            //设置confirm
            rabbitTemplate.setConfirmCallback(confirmCallback);
            //设置return机制
            rabbitTemplate.setReturnCallback(returnCallback);

            rabbitTemplate.convertAndSend("myExchange","info.order",json);

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


    /**
     * 查询地址 与选中商品信息
     * @param orderId
     * @return
     */
    @GetMapping("/query")
    public Map<String, String> query(@RequestParam("orderId") String orderId) {
        return payService.query(orderId);
    }





    /**
     * return 机制 没找到交换机或路由 就走这里
     */
    public RabbitTemplate.ReturnCallback returnCallback = new RabbitTemplate.ReturnCallback() {
        @Override
        public void returnedMessage(Message message, int replyCode, String replyText,
                                    String exchange, String routingKey) {

            System.out.println("message" + message);
            System.out.println("exchange" + exchange);
            System.out.println("replyCode" + replyCode);
            System.out.println("replyText" + replyText);
            System.out.println("routingKey" + routingKey);

        }
    };


    /**
     * confirm确认
     */
    public RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String s) {
            if (ack) {
                System.out.println("已接收");
            }else {
                System.out.println("程序执行失败");
            }
        }
    };




    /**
     * 使用mq发送消息
     * @param json
     */
    public void sendMQ(String json) {
        //调用工厂类的连接方法 拿到连接
        Connection connection = MyConnectionFactory.create();
        try {
            //创建与 Exchange的通道 每个连接可以创建多个通道 每个通道代表一个会话任务
            Channel channel = connection.createChannel();
            //声明交换机
            channel.exchangeDeclare("myExchange", BuiltinExchangeType.DIRECT);
            //声明队列
            channel.queueDeclare("updateOrderQueue", true, false, false, null);
            //绑定交换机和队列
            channel.queueBind("updateOrderQueue","myExchange","updateOrder");
            //调用basicPublish() 发送数据
            channel.basicPublish("myExchange", "updateOrder", null, json.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
