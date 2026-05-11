package com.cloudcompare.ai.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 🚀 OVER-EXCELLENCE PERFORMANCE CACHE
 * Replaced slow Collections.synchronizedMap with Caffeine.
 * Caffeine provides near-optimal hit rates and massive concurrent throughput.
 */
@Service
public class CacheService {

    private final Cache<String, Object> cache;

    public CacheService() {
        this.cache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .recordStats() // Optional: useful for metrics later
                .build();
    }

    public Object get(String key) {
        return cache.getIfPresent(key);
    }

    public void set(String key, Object value) {
        cache.put(key, value);
    }

    public void clear() {
        cache.invalidateAll();
    }
}
