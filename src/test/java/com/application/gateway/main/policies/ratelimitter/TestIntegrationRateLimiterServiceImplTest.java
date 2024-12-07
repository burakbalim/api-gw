package com.application.gateway.main.policies.ratelimitter;

import com.application.gateway.BaseTest;
import com.application.gateway.common.exception.RateLimitExceedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class  TestIntegrationRateLimiterServiceImplTest extends BaseTest {

    @Autowired
    private RateLimiterServiceImpl rateLimiterService;

    @Test
    void testTryConsume() {
        RateLimit rateLimit = new RateLimit();
        rateLimit.setKey("test_2");
        rateLimit.setRate(10);
        rateLimit.setPer(5);
        rateLimit.setQuotaMax(10);
        rateLimit.setQuotaRenewalRate(60);

        rateLimiterService.remove(rateLimit.getKey());

        rateLimiterService.configure(rateLimit);

        for (int i = 1; i <= 10; i++) {
            rateLimiterService.tryConsume(rateLimit.getKey());
        }
        assertThrows(RateLimitExceedException.class, () -> rateLimiterService.tryConsume(rateLimit.getKey()));
    }
}