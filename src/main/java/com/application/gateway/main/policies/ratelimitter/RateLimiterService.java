package com.application.gateway.main.policies.ratelimitter;

public interface RateLimiterService {

    void configure(RateLimit rateLimit);

    void tryConsume(String key);

    void remove(String key);
}
