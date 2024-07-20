package com.iori.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全局过滤器
 */
@Component
public class PowerFilter implements GlobalFilter, Ordered {

    private static final String AUTHORIZE_TOKEN = "Authorization";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //获取访问路径
        String url = request.getURI().getPath();
        //判断路径是不是以 /user/login 开头 如果是放行 不是拦截
        if (url.startsWith("/api/pc/login")) {
        //if (url.startsWith("/api/tb-user-model/queryById")) {
            return chain.filter(exchange);
        }
        String token = request.getHeaders().getFirst("token");

        if (StringUtils.isEmpty(token)) {
            token = request.getQueryParams().getFirst("token");
        }

        if (StringUtils.isEmpty(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //将请求token传递到下一个微服务 结构必须是 Authorization bearer token
        request.mutate().header(AUTHORIZE_TOKEN,"bearer " + token);
        return chain.filter(exchange);


       /* System.out.println("token验证...........");
        //从请求中取出第一个token的值
        String token = exchange.getRequest().getQueryParams().getFirst("token");
        //如果token为 null或者""就设置一个状态码并且终止
        if (StringUtils.isEmpty(token)) {
            //设置状态码
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            //终止
            return exchange.getResponse().setComplete();
        }
        //验证通过将请求转发到用户的控制器
        return chain.filter(exchange);*/
    }

    /**
     * 根据返回的值决定过滤器优先级 越小优先级越高
     * @return
     */
    @Override
    public int getOrder() {
        return -1;
    }
}
