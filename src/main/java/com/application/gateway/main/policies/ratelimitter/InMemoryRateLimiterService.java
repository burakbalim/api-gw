package com.application.gateway.main.policies.ratelimitter;

import com.application.gateway.common.exception.RateLimitExceedException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value="rate.limit.distribution.enabled", havingValue = "false")
public class InMemoryRateLimiterService implements RateLimiterService {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public void configure(RateLimit rateLimit) {
        buckets.put(rateLimit.getKey(), getBucket(rateLimit));
    }

    @Override
    public void tryConsume(String key) {
        Optional.ofNullable(buckets.get(key)).ifPresent(bucket -> {
            if (!bucket.tryConsume(1)) {
                throw new RateLimitExceedException(key);
            }
        });
    }

    @Override
    public void remove(String key) {
        buckets.remove(key);
    }

    private Bucket getBucket(RateLimit rateLimit) {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(rateLimit.getRate(),
                        Refill.intervally(rateLimit.getRate(), Duration.ofSeconds(rateLimit.getPer()))))
                .addLimit(Bandwidth.classic(rateLimit.getQuotaMax(),
                        Refill.intervally(rateLimit.getQuotaMax(), Duration.ofSeconds(rateLimit.getQuotaRenewalRate()))))
                .build();
    }
}
