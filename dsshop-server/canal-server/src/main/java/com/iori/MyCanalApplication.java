package com.iori;

import com.iori.util.CanalUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class MyCanalApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(MyCanalApplication.class, args);
        CanalUtil canalUtil = run.getBean(CanalUtil.class);
        canalUtil.main();
    }



    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
