package com.application.gateway.common.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@ConditionalOnProperty(value = "cache.custom.enabled", havingValue = "true")
public class CacheProvider<T, K> {

    private final Cache<T, K> cache;

    public CacheProvider(Cache<T, K> cache) {
        this.cache = cache;
    }

    public void put(T key, K value) {
        cache.put(key, value);
    }

    public void putWithTTL(T key, K value, long secondsTTL) {
        cache.put(key, value, secondsTTL);
    }

    public Optional<K> get(T key) {
        return Optional.ofNullable(cache.get(key));
    }

    public void remove(T key) {
        cache.remove(key);
    }

    public Object getConnectionInstance() {
        return cache.getConnectionInstance();
    }
}
