package com.iori.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfig {


    public static final String addCourseExchange = "addCourseExchange";
    public static final String addCourseQueue = "addCourseQueue";
    public static final String addCourseRouting = "addCourseRouting";


    /**
     * 创建交换机
     *
     * @return
     */
    @Bean(addCourseExchange)
    public Exchange addCourseExchange() {
        return ExchangeBuilder.topicExchange(addCourseExchange).durable(true).build();
    }

    /**
     * 创建队列
     *
     * @return
     */
    @Bean(addCourseQueue)
    public Queue addCourseQueue() {
        return new Queue(addCourseQueue, true, false, false, null);
    }

    /**
     * 交换机与队列绑定
     *
     * @return
     */
    @Bean
    public Binding bindingCourseQueueToCourseExchange(@Qualifier(addCourseExchange) Exchange exchange,
                                                      @Qualifier(addCourseQueue) Queue queue) {

        return BindingBuilder.bind(queue).to(exchange).with(addCourseRouting).noargs();

    }

    /************************************************************/
    public static final String addSuccessExchange = "addSuccessExchange";
    public static final String addSuccessQueue = "addSuccessQueue";
    public static final String addSuccessRouting = "addSuccessRouting";


    /**
     * 创建交换机
     *
     * @return
     */
    @Bean(addSuccessExchange)
    public Exchange addSuccessExchange() {
        return ExchangeBuilder.topicExchange(addSuccessExchange).durable(true).build();
    }

    /**
     * 创建队列
     *
     * @return
     */
    @Bean(addSuccessQueue)
    public Queue addSuccessQueue() {
        return new Queue(addSuccessQueue, true, false, false, null);
    }

    /**
     * 交换机与队列绑定
     *
     * @return
     */
    @Bean
    public Binding bindingSuccessQueueToCourseExchange(@Qualifier(addSuccessExchange) Exchange exchange,
                                                      @Qualifier(addSuccessQueue) Queue queue) {

        return BindingBuilder.bind(queue).to(exchange).with(addSuccessRouting).noargs();

    }


}
