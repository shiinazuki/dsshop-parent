package com.iori;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})//这里排除掉
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
public class MyOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyOrderApplication.class,args);
    }
}
