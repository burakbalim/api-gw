package com.application.gateway.common.cache.redis;

import com.application.gateway.common.cache.Cache;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@ConditionalOnProperty(value = "cache.custom.redis.enabled", havingValue = "true")
@RequiredArgsConstructor
@Service
public class RedisCache<T, K> implements Cache<T, K> {

    private final JedisConnectionFactory jedisConnectionFactory;

    private RedisTemplate<T, K> template;

    @PostConstruct
    public void init() {
        template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
    }

    @Override
    public void put(T key, K value) {
        template.opsForValue().set(key, value);
    }

    @Override
    public void put(T key, K value, long ttlSeconds) {
        template.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
    }

    @Override
    public K get(T key) {
        return template.opsForValue().get(key);
    }

    @Override
    public void remove(T key) {
        RedisConnection connection = jedisConnectionFactory.getConnection();
        Set<byte[]> patternResultConf = connection.keys(key.toString().getBytes());
        if(Objects.nonNull(patternResultConf) && !patternResultConf.isEmpty()) {
            connection.del(patternResultConf.toArray(new byte[0][]));
        }
    }

    @Override
    public Set<T> getAllKeys(T cachePrefix) {
        return template.keys(cachePrefix);
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public Object getConnectionInstance() {
        try {
            Field pool = JedisConnectionFactory.class.getDeclaredField("pool");
            pool.setAccessible(true);
            return pool.get(jedisConnectionFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
