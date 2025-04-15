package com.application.gateway.common.cache;

import java.util.Set;

public interface Cache<T, K> {
    void put(T key, K value);

    void put(T key, K value, long ttlMin);

    K get(T key);

    void remove(T key);

    Set<T> getAllKeys(T cachePrefix);

    Object getConnectionInstance();
}
