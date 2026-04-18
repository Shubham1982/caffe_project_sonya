package org.example.caffe.service;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CacheService {

    private final CacheManager cacheManager;

    public CacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void clearAllCaches() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        });
    }

    public Map<String, Object> getAllCacheData() {
        Map<String, Object> cacheData = new HashMap<>();
        cacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cacheData.put(cacheName, cache.getNativeCache());
            }
        });
        return cacheData;
    }
}
