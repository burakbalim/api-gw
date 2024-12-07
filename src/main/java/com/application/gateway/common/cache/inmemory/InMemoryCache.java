package com.application.gateway.common.cache.inmemory;

import com.application.gateway.common.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ConditionalOnProperty(value="cache.custom.redis.enabled", havingValue = "false")
@RequiredArgsConstructor
@Service
public class InMemoryCache<T,K> implements Cache<T,K> {

    private final Map<T, K> hashMap = new HashMap<>();

    @Override
    public void put(T key, K value) {
        hashMap.put(key, value);
    }

    @Override
    public void put(T key, K value, long ttlMin) {
        hashMap.put(key, value);
    }

    @Override
    public K get(T key) {
        return hashMap.get(key);
    }

    @Override
    public void remove(T key) {
        hashMap.remove(key);
    }

    @Override
    public Set<T> getAllKeys(T cachePrefix) {
        return null;
    }

    @Override
    public Object getConnectionInstance() {
        return null;
    }
}
