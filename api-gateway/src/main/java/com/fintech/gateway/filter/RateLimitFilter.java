package com.fintech.gateway.filter;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Component
public class RateLimitFilter {

    private final ReactiveStringRedisTemplate redis;

    public RateLimitFilter(ReactiveStringRedisTemplate redis) {
        this.redis = redis;
    }

    public Mono<Boolean> isAllowed(
            String key,
            int limit,
            Duration window) {

        return redis.opsForValue()
                .increment(key)
                .flatMap(count -> {
                    if (count == 1) {
                        return redis.expire(key, window)
                                .thenReturn(true);
                    }
                    return Mono.just(count <= limit);
                });
    }

    public static String buildKey(
            String identifier,
            String path) {

        long minute = Instant.now().getEpochSecond() / 60;
        return "rate:" + identifier + ":" + path + ":" + minute;
    }
}
