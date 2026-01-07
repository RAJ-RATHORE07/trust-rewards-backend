package com.fintech.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()

                .route("auth-service", r -> r
                        .path("/auth/**")
                        .uri("http://auth-service:8081"))

                .route("payment-service", r -> r
                        .path("/payments/**")
                        .uri("http://payment-service:8082"))

                .route("trust-score-service", r -> r
                        .path("/trust/**")
                        .uri("http://trust-score-service:8083"))

                .build();
    }
}
