package com.application.gateway.integration.main.policies.ratelimitter;

import com.application.gateway.BaseTest;
import com.application.gateway.common.exception.RateLimitExceedException;
import com.application.gateway.main.policies.ratelimitter.RateLimit;
import com.application.gateway.main.policies.ratelimitter.DistributionRateLimiterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TestIntegrationDistributionRateLimiterServiceTest extends BaseTest {

    @Autowired
    private DistributionRateLimiterService rateLimiterService;

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
