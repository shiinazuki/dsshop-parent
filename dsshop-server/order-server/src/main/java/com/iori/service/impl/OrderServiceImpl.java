package com.iori.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iori.bean.Order;
import com.iori.bean.OrderItem;
import com.iori.client.SkuFeignClient;
import com.iori.mapper.OrderMapper;
import com.iori.service.CartService;
import com.iori.service.OrderItemService;
import com.iori.service.OrderService;
import com.iori.util.MyConnectionFactory;
import com.iori.vo.OrderVo;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private CartService cartService;
    @Autowired
    private SkuFeignClient skuFeignClient;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GlobalTransactional  //加上这个注解就行了
    @Override
    public boolean create(OrderVo orderVo, String username) {

        //创建订单对象
        Order order = new Order();
        order.setId(IdWorker.getIdStr());
        order.setPayType(orderVo.getPayType());
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        order.setUsername(username);
        order.setBuyerMessage(orderVo.getPayType());
        order.setReceiverContact(orderVo.getContact());
        order.setReceiverMobile(orderVo.getMobile());
        order.setReceiverAddress(orderVo.getAddress());
        order.setSourceType("1");
        order.setOrderStatus("0");
        order.setPayStatus("0");
        order.setConsignStatus("0");
        order.setPayType(orderVo.getPayType());


        int totalNum = 0;
        int totalMoney = 0;

        String[] ids = orderVo.getIds();
        for (String id : ids) {
            OrderItem orderItem = (OrderItem) redisTemplate.boundHashOps("cart:" + username).get(id);
            //设置订单编号
            orderItem.setOrderId(order.getId());
            totalNum += orderItem.getNum();
            totalMoney += orderItem.getMoney();
            //修改库存
            skuFeignClient.updateNum(orderItem.getNum(), orderItem.getSkuId());
            //保存购物车信息数据库
            orderItemService.save(orderItem);
        }

        order.setTotalNum(totalNum);
        order.setTotalMoney(totalMoney);
        //将订单数据加入到数据库
        baseMapper.insert(order);

        //删除购物车选中的数据
        cartService.remove(ids, username);

        //将订单编号放到消息队列
        //orderTime(order.getId());
        rabbitTemplate.convertAndSend("placeOrderExchange", "info.one", order.getId(),
                new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                //设置超时时间和字符集
                String timeOut = 5 * 60 * 1000 + "";
                message.getMessageProperties().setExpiration(timeOut);
                message.getMessageProperties().setContentEncoding("UTF-8");
                return message;
            }
        });

        return true;
    }


    /**
     * 用户下完订单后 将该订单信息加入队列 并设置信息存活时间为10分钟
     *
     * @param msg
     */
    public void orderTime(String msg) {

        //创建连接
        Connection connection = MyConnectionFactory.create();
        try {
            //拿到管道
            Channel channel = connection.createChannel();
            //创建交换机
            channel.exchangeDeclare("placeOrderExchange", BuiltinExchangeType.TOPIC);
            //创建map集合 用来保存需要转移到下一个交换机的名称
            Map<String, Object> map = new HashMap<>();
            map.put("x-dead-letter-exchange", "failOrderExchange");
            //创建队列
            channel.queueDeclare("placeOrderQueue", true, false, false, map);
            //绑定
            channel.queueBind("placeOrderQueue", "placeOrderExchange", "info.order");
            //设置消息有效期
            String activeTime = (3 * 60 * 1000) + "";
            AMQP.BasicProperties basicProperties = new AMQP.BasicProperties()
                    .builder().deliveryMode(2)
                    .contentEncoding("UTF-8")
                    .expiration(activeTime)
                    .build();

            //订阅队列
            channel.basicPublish("placeOrderExchange", "info.order",
                    basicProperties, msg.getBytes());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}
