package com.iori.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MQConfig {

    public static final String ORDER_EXCHANGE = "myExchange";
    public static final String ORDER_QUEUE = "updateOrderQueue";

    public static final String ONE_EXCHANGE = "placeOrderExchange";
    public static final String ONE_QUEUE = "placeOrderQueue";

    public static final String TWO_EXCHANGE = "failOrderExchange";
    public static final String TWO_QUEUE = "failOrderQueue";
    public static final String TWO_ROUTING = "#";


    /**
     * 创建交换机
     * @return
     */
    @Bean(ORDER_EXCHANGE)
    public Exchange createOederExchange() {
        return ExchangeBuilder.topicExchange(ORDER_EXCHANGE).durable(true).build();
    }

    /**
     * 创建队列
     * @return
     */
    @Bean(ORDER_QUEUE)
    public Queue createOrderQueue() {
        return new Queue(ORDER_QUEUE,true,false,false,null);
    }

    /**
     * 交换机与队列绑定
     * @return
     */
    @Bean
    public Binding bindingOrderQueueToExchange(@Qualifier(ORDER_EXCHANGE) Exchange exchange,
                                            @Qualifier(ORDER_QUEUE) Queue queue) {

        return BindingBuilder.bind(queue).to(exchange).with("info.order").noargs();

    }

/*******************************************************************************/


    /**
     * 创建交换机
     * @return
     */
    @Bean(ONE_EXCHANGE)
    public Exchange oneOederExchange() {
        return ExchangeBuilder.topicExchange(ONE_EXCHANGE).durable(true).build();
    }

    /**
     * 创建队列
     * @return
     */
    @Bean(ONE_QUEUE)
    public Queue oneOrderQueue() {
        //加入死信 消息到时转到哪里
        Map<String,Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange",TWO_EXCHANGE);
        map.put("x-dead-letter-routing-key",TWO_ROUTING);
        return new Queue(ONE_QUEUE,true,false,false,map);
    }

    /**
     * 交换机与队列绑定
     * @return
     */
    @Bean
    public Binding bindingOneOrderQueueToOneExchange(@Qualifier(ONE_EXCHANGE) Exchange exchange,
                                            @Qualifier(ONE_QUEUE) Queue queue) {

        return BindingBuilder.bind(queue).to(exchange).with("info.one").noargs();

    }

    /*******************************************************************************/


    /**
     * 创建交换机
     * @return
     */
    @Bean(TWO_EXCHANGE)
    public Exchange twoOederExchange() {
        return ExchangeBuilder.topicExchange(TWO_EXCHANGE).durable(true).build();
    }

    /**
     * 创建队列
     * @return
     */
    @Bean(TWO_QUEUE)
    public Queue twoOrderQueue() {
        return new Queue(TWO_QUEUE,true,false,false,null);
    }

    /**
     * 交换机与队列绑定
     * @return
     */
    @Bean
    public Binding bindingTwoOrderQueueToTwoExchange(@Qualifier(TWO_EXCHANGE) Exchange exchange,
                                                     @Qualifier(TWO_QUEUE) Queue queue) {

        return BindingBuilder.bind(queue).to(exchange).with(TWO_ROUTING).noargs();

    }




}
