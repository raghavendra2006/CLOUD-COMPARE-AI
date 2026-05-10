package com.cloudcompare.ai.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CacheServiceTest {

    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        cacheService = new CacheService();
    }

    @Test
    void testCacheOperations() {
        cacheService.set("key1", "value1");
        assertEquals("value1", cacheService.get("key1"));

        cacheService.set("key2", "value2", 1000L);
        assertEquals("value2", cacheService.get("key2"));

        cacheService.clear();
        assertNull(cacheService.get("key1"));
    }
}
