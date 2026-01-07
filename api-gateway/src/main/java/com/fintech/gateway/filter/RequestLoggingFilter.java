package com.fintech.gateway.filter;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RequestLoggingFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(
            org.springframework.web.server.ServerWebExchange exchange,
            org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        System.out.println(
                "➡️ " + exchange.getRequest().getMethod() + " " +
                        exchange.getRequest().getURI());

        return chain.filter(exchange);
    }
}
