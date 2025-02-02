package com.application.gateway.unit.main.policies.ratelimitter;

import com.application.gateway.common.cache.CacheProvider;
import com.application.gateway.common.exception.RateLimitExceedException;
import com.application.gateway.main.policies.ratelimitter.RateLimit;
import com.application.gateway.main.policies.ratelimitter.DistributionRateLimiterService;
import io.github.bucket4j.distributed.BucketProxy;
import io.github.bucket4j.redis.jedis.cas.JedisBasedProxyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import redis.clients.jedis.JedisPool;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DistributionRateLimiterServiceTest {

    @Mock
    private CacheProvider<String, RateLimit> cacheProvider;

    @Mock
    private JedisBasedProxyManager.JedisBasedProxyManagerBuilder proxyManagerBuilder;

    @Mock
    private JedisBasedProxyManager proxyManager;

    @Mock
    private BucketProxy bucketProxy;

    @InjectMocks
    private DistributionRateLimiterService rateLimiterService;


    @BeforeEach
    void setUp() {
        when(cacheProvider.getConnectionInstance()).thenReturn(mock(JedisPool.class));
        when(proxyManagerBuilder.withExpirationStrategy(any())).thenReturn(proxyManagerBuilder);
        when(proxyManagerBuilder.build()).thenReturn(proxyManager);
    }

    @Test
    void testInit() {
        // Initialize the service
        rateLimiterService.init();

        // Verify that the JedisBasedProxyManager is built
        verify(proxyManagerBuilder).build();
    }

    @Test
    void testConfigure() {
        RateLimit rateLimit = mock(RateLimit.class);
        when(rateLimit.getKey()).thenReturn("testKey");

        rateLimiterService.configure(rateLimit);

        // Verify that the rate limit is put into the cache
        verify(cacheProvider).put(eq("testKey"), eq(rateLimit));

        // Verify that the bucket is reset
        verify(proxyManagerBuilder).build();
    }

    @Test
    void testTryConsumeRateLimitExceeds() {
        String key = "testKey";
        RateLimit rateLimit = mock(RateLimit.class);
        when(cacheProvider.get(key)).thenReturn(Optional.of(rateLimit));
        BucketProxy bucketProxy = mock(BucketProxy.class);
        when(bucketProxy.tryConsume(1)).thenReturn(false);
        when(proxyManagerBuilder.withExpirationStrategy(any())).thenReturn(proxyManagerBuilder);
        when(proxyManagerBuilder.build()).thenReturn(proxyManager);

        assertThrows(RateLimitExceedException.class, () -> rateLimiterService.tryConsume(key));

        // Verify that tryConsume was called on the bucket
        verify(bucketProxy).tryConsume(1);
    }

    @Test
    void testTryConsumeRateLimitSuccess() {
        String key = "testKey";
        RateLimit rateLimit = mock(RateLimit.class);
        when(cacheProvider.get(key)).thenReturn(Optional.of(rateLimit));
        BucketProxy bucketProxy = mock(BucketProxy.class);
        when(bucketProxy.tryConsume(1)).thenReturn(true);
        when(proxyManagerBuilder.withExpirationStrategy(any())).thenReturn(proxyManagerBuilder);
        when(proxyManagerBuilder.build()).thenReturn(proxyManager);

        rateLimiterService.tryConsume(key);

        // Verify that tryConsume was called and the exception was not thrown
        verify(bucketProxy).tryConsume(1);
    }

    @Test
    void testRemove() {
        String key = "testKey";
        String bucketKey = key + "_rate_limiter";

        rateLimiterService.remove(key);

        // Verify that the cache remove method is called for the key and bucketKey
        verify(cacheProvider).remove(eq(key));
        verify(cacheProvider).remove(eq(bucketKey));
    }
}
