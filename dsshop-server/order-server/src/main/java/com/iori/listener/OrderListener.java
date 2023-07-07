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

    }

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
                    Map<String,Object> data = JSON.parseObject(msg, Map.class);
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

}
