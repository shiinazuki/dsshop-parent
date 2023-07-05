package com.iori.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class LimitConfig {

    /**
     * 按用户限流
     * @return
     */
    /*
    @Bean
    public KeyResolver userKeyResolver() {
        return new KeyResolver() {
            @Override
            public Mono<String> resolve(ServerWebExchange exchange) {
                //按照请求路径来匹配
                String user = exchange.getRequest().getQueryParams().getFirst("token");
                System.out.println("===========>" + user);
                return Mono.just(user);
            }
        };
    }
    */

    /**
     * 按路径进行限流
     * @return
     */
    @Bean
    public KeyResolver pathKeyResolver() {
        return new KeyResolver() {
            @Override
            public Mono<String> resolve(ServerWebExchange exchange) {
                //按照请求路径来匹配
                String path = exchange.getRequest().getPath().toString();
                System.out.println("===========>" + path);
                return Mono.just(path);
            }
        };
    }

}
