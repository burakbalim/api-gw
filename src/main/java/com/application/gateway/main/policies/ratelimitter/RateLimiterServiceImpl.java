package com.application.gateway.main.policies.ratelimitter;

import com.application.gateway.common.cache.CacheProvider;
import com.application.gateway.common.exception.RateLimitExceedException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.BucketProxy;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.redis.jedis.cas.JedisBasedProxyManager;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class RateLimiterServiceImpl implements RateLimiterService {

    private static final String BUCKET_KEY = "_rate_limiter";

    private final CacheProvider<String, RateLimit> cacheProvider;

    private JedisBasedProxyManager.JedisBasedProxyManagerBuilder proxyManager;

    @PostConstruct
    public void init() {
        proxyManager = JedisBasedProxyManager.builderFor((JedisPool) cacheProvider.getConnectionInstance());
    }

    @Override
    public void configure(RateLimit rateLimit) {
        cacheProvider.put(rateLimit.getKey(), rateLimit);
        getBucketProxy(rateLimit).reset();
    }

    @Override
    public void tryConsume(String key) {
        Optional<RateLimit> rateLimitOptional = cacheProvider.get(key);
        if (rateLimitOptional.isPresent()) {
            BucketProxy bucketProxy = getBucketProxy(rateLimitOptional.get());
            if (!bucketProxy.tryConsume(1)) {
                throw new RateLimitExceedException(key);
            }
        }
    }

    @Override
    public void remove(String key) {
        String bucketKey = key + BUCKET_KEY;
        cacheProvider.remove(key);
        cacheProvider.remove(bucketKey);
    }

    private BucketProxy getBucketProxy(RateLimit rateLimit) {
        String bucketKey = rateLimit.getKey() + BUCKET_KEY;
        return proxyManager.withExpirationStrategy(ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofSeconds(10))).build().
                builder().build(bucketKey.getBytes(), getConfigSupplier(rateLimit));
    }

    private Supplier<BucketConfiguration> getConfigSupplier(RateLimit rateLimit) {
        return () -> (BucketConfiguration.builder()
                .addLimit(Bandwidth.classic(rateLimit.getRate(), Refill.intervally(rateLimit.getRate(), Duration.ofSeconds(rateLimit.getPer()))))
                .addLimit(Bandwidth.classic(rateLimit.getQuotaMax(), Refill.intervally(rateLimit.getQuotaMax(), Duration.ofSeconds(rateLimit.getQuotaRenewalRate()))))
                .build());
    }
}
