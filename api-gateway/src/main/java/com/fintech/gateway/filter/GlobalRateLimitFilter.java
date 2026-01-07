package com.fintech.gateway.filter;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class GlobalRateLimitFilter implements GlobalFilter, Ordered {

    private final RateLimitFilter rateLimit;

    public GlobalRateLimitFilter(RateLimitFilter rateLimit) {
        this.rateLimit = rateLimit;
    }

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        String path = exchange.getRequest().getPath().toString();

        // -------- LOGIN RATE LIMIT (BY IP) --------
        if (path.startsWith("/auth/login")) {

            String ip = exchange.getRequest()
                    .getRemoteAddress()
                    .getAddress()
                    .getHostAddress();

            String key = RateLimitFilter.buildKey(ip, "login");

            return rateLimit.isAllowed(key, 5, Duration.ofMinutes(1))
                    .flatMap(allowed -> allowed
                            ? chain.filter(exchange)
                            : reject(exchange));
        }

        // -------- PAYMENT RATE LIMIT (BY USER) --------
        if (path.startsWith("/payments")) {

            String userId = exchange.getRequest()
                    .getHeaders()
                    .getFirst("X-User-Id");

            if (userId == null) {
                return reject(exchange);
            }

            String key = RateLimitFilter.buildKey(userId, "payments");

            return rateLimit.isAllowed(key, 10, Duration.ofMinutes(1))
                    .flatMap(allowed -> allowed
                            ? chain.filter(exchange)
                            : reject(exchange));
        }

        // -------- DEFAULT LIMIT --------
        String userId = exchange.getRequest()
                .getHeaders()
                .getFirst("X-User-Id");

        if (userId != null) {
            String key = RateLimitFilter.buildKey(userId, "default");

            return rateLimit.isAllowed(key, 100, Duration.ofMinutes(1))
                    .flatMap(allowed -> allowed
                            ? chain.filter(exchange)
                            : reject(exchange));
        }

        return chain.filter(exchange);
    }

    private Mono<Void> reject(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1; // execute before other filters
    }
}
