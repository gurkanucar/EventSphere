package com.gucardev.eventsphere.infrastructure.config.cache;

import java.util.Arrays;
import java.util.List;

// Example usage:
//@Cacheable(value = CacheNames.CACHE_NAME, key = "#parameter", cacheManager = RedisCacheConfig.REDIS_CACHE_MANAGER_MEDIUM_LIVED)
//@CachePut(value = CacheNames.CACHE_PRODUCTS, key = "#product.id", cacheManager = RedisCacheConfig.REDIS_CACHE_MANAGER_MEDIUM_LIVED)
//@CacheEvict(value = CacheNames.CACHE_PRODUCTS, key = "#id", cacheManager = RedisCacheConfig.REDIS_CACHE_MANAGER_MEDIUM_LIVED)
//@CacheEvict(value = CacheNames.CACHE_PRODUCTS, allEntries = true, cacheManager = RedisCacheConfig.REDIS_CACHE_MANAGER_MEDIUM_LIVED)
//public void clearCache() {log.info("Clearing all products from cache");}

public class CacheNames {

    public static final String CACHE_USER_INFO = "user_info";
    public static final String CACHE_USER_PHOTO = "user_photo";
    public static final String CACHE_ALL_LOOKUPS = "all_lookups";

    public static List<String> getAllCacheNames() {
        return Arrays.asList(
                CACHE_USER_INFO,
                CACHE_USER_PHOTO,
                CACHE_ALL_LOOKUPS
        );
    }
}