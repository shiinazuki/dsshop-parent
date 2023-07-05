package com.iori;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class MyItemWebApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(MyItemWebApplication.class, args);


    }
}
