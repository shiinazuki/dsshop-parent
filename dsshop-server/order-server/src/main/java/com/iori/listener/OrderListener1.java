package com.iori.listener;

import com.iori.bean.Order;
import com.iori.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OrderListener1 {


    @Autowired
    private OrderService orderService;


    /**
     * 监听 updateOrderQueue 拿到数据进行数据修改
     * @param map
     * @param channel
     * @param message
     */
    @RabbitListener(queues = "updateOrderQueue")
    public void reviceOrder(Map<String, String> map, Channel channel, Message message) {

        //拿到订单编号
        String orderId = map.get("out_trade_no");
        //先查询出来
        Order order = orderService.getById(orderId);
        //修改状态
        order.setOrderStatus("1");
        order.setPayStatus("1");
        //调用修改方法
        orderService.updateById(order);

    }


    /**
     * 监听 failOrderQueue 失败的队列 拿到数据修改状态
     * @param msg
     */
    @RabbitListener(queues = "failOrderQueue")
    public void checkOrderPayStatus(String msg) {
        //根据拿到的编号 查询数据库
        Order order = orderService.getById(msg);
        //判断订单状态是否为1 如果不为则待变订单失效
        if (!"1".equals(order.getPayStatus())) {
            //将订单状态改为失效状态
            order.setOrderStatus("2");
            orderService.updateById(order);
        }
    }


}
