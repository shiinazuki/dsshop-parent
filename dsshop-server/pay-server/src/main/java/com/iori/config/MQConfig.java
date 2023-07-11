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





}
