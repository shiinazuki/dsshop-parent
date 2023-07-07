package com.iori.util;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 获取连接的工具类
 */
public class MyConnectionFactory {

    public static Connection create() {

        //声明 connection 和  channel
        Connection connection = null;

        //创建连接工厂对象
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //设置主机
        connectionFactory.setHost("127.0.0.1");
        //设置端口
        connectionFactory.setPort(5672);
        //设置虚拟主机
        connectionFactory.setVirtualHost("/");
        //设置账号和密码
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        try {
            //创建与RabbitMQ服务的TCP连接
            connection = connectionFactory.newConnection();
            //返回连接
            return connection;

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }


    }

}
