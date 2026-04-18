package org.example.caffe.controller;

import org.example.caffe.service.CacheService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cache")
public class CacheController {

    private final CacheService cacheService;

    public CacheController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @DeleteMapping("/clear")
    public String clearCache() {
        cacheService.clearAllCaches();
        return "All caches cleared successfully";
    }

    @GetMapping("/data")
    public Map<String, Object> getCacheData() {
        return cacheService.getAllCacheData();
    }
}
