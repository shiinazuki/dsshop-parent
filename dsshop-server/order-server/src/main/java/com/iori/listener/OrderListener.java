package com.iori.listener;

import com.alibaba.fastjson.JSON;
import com.iori.bean.Order;
import com.iori.service.OrderService;
import com.iori.util.MyConnectionFactory;
import com.rabbitmq.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * 实现 CommandLineRunner接口里的run方法 使 SpringBoot程序启动时调用
 */
@Component
public class OrderListener implements CommandLineRunner {

    @Autowired
    private OrderService orderService;

    @Override
    public void run(String... args) throws Exception {

        this.reviceOrder();
        this.unreviceOrder();

    }

    /**
     * 监听成功支付订单 蒋订单状态修改为1
     */
    public void reviceOrder() {
        //拿到连接
        Connection connection = MyConnectionFactory.create();

        try {
            //拿到管道
            Channel channel = connection.createChannel();
            //创建DefaultConsumer 对象 并实现内部类里的 handleDelivery()
            DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag,
                                           Envelope envelope,
                                           AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    //拿到消息
                    String msg = new String(body, "UTF-8");
                    //把拿到的消息转为map
                    Map<String, Object> data = JSON.parseObject(msg, Map.class);
                    //拿到订单编号
                    String orderId = data.get("out_trade_no").toString();
                    //先查询出来
                    Order order = orderService.getById(orderId);
                    //修改状态
                    order.setOrderStatus("1");
                    order.setPayStatus("1");
                    //调用修改方法
                    orderService.updateById(order);
                }
            };

            //订阅队列 准备拿哪个队列的消息 true为拿完数据后 清除掉
            channel.basicConsume("updateOrderQueue", true, defaultConsumer);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * 监听未支付订单 蒋订单状态修改为2
     */
    public void unreviceOrder() {

        //拿到连接
        Connection connection = MyConnectionFactory.create();
        try {
            //拿到管道
            Channel channel = connection.createChannel();
            //创建交换机
            channel.exchangeDeclare("failOrderExchange", BuiltinExchangeType.TOPIC);
            //创建队列
            channel.queueDeclare("failOrderQueue", true, false, false, null);
            //交换机与队列绑定
            channel.queueBind("failOrderQueue", "failOrderExchange", "#");
            //创建 DefaultConsumer 对象
            DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag,
                                           Envelope envelope,
                                           AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {

                    //拿到消息
                    String msg = new String(body, "UTF-8");
                    //根据拿到的编号 查询数据库
                    Order order = orderService.getById(msg);
                    //判断订单状态是否为1 如果不为则待变订单失效
                    if (!"1".equals(order.getPayStatus())) {
                        //将订单状态改为失效状态
                        order.setOrderStatus("2");
                        orderService.updateById(order);
                    }
                }
            };

            //订阅队列
            channel.basicConsume("failOrderQueue", true, defaultConsumer);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
