package com.iori.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MQConfig {


    public static final String ONE_EXCHANGE = "oneExchange";
    public static final String ONE_QUEUE = "oneQueue";
    public static final String ONE_ROUTING = "info.one";


    public static final String TWO_EXCHANGE = "twoExchange";
    public static final String TWO_QUEUE = "twoQueue";
    public static final String TWO_ROUTING = "info.two";


    /**
     * 创建交换机
     *
     * @return
     */
    @Bean(ONE_EXCHANGE)
    public Exchange oneOederExchange() {
        return ExchangeBuilder.topicExchange(ONE_EXCHANGE).durable(true).build();
    }

    /**
     * 创建队列
     *
     * @return
     */
    @Bean(ONE_QUEUE)
    public Queue oneOrderQueue() {
        //加入死信 消息到时转到哪里
        Map<String, Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange", TWO_EXCHANGE);
        map.put("x-dead-letter-routing-key", TWO_ROUTING);
        return new Queue(ONE_QUEUE, true, false, false, map);
    }

    /**
     * 交换机与队列绑定
     *
     * @return
     */
    @Bean
    public Binding bindingOneOrderQueueToOneExchange(@Qualifier(ONE_EXCHANGE) Exchange exchange,
                                                     @Qualifier(ONE_QUEUE) Queue queue) {

        return BindingBuilder.bind(queue).to(exchange).with(ONE_ROUTING).noargs();

    }

    /*******************************************************************************/


    /**
     * 创建交换机
     *
     * @return
     */
    @Bean(TWO_EXCHANGE)
    public Exchange twoOederExchange() {
        return ExchangeBuilder.topicExchange(TWO_EXCHANGE).durable(true).build();
    }

    /**
     * 创建队列
     *
     * @return
     */
    @Bean(TWO_QUEUE)
    public Queue twoOrderQueue() {
        return new Queue(TWO_QUEUE, true, false, false, null);
    }

    /**
     * 交换机与队列绑定
     *
     * @return
     */
    @Bean
    public Binding bindingTwoOrderQueueToTwoExchange(@Qualifier(TWO_EXCHANGE) Exchange exchange,
                                                     @Qualifier(TWO_QUEUE) Queue queue) {

        return BindingBuilder.bind(queue).to(exchange).with(TWO_ROUTING).noargs();

    }


}
