package com.bookkeeping.infrastructure.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public SimpleCacheManager cacheManager() {
        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(java.util.Collections.singletonList(
            new org.springframework.cache.concurrent.ConcurrentMapCache("default")
        ));
        return manager;
    }
}
