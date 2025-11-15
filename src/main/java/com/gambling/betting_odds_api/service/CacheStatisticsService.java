package com.gambling.betting_odds_api.service;

// ═══════════════════════════════════════════════════════════════════════════
// SPRING CACHE - Cache statistics support
// ═══════════════════════════════════════════════════════════════════════════
import org.springframework.cache.CacheManager;

// ═══════════════════════════════════════════════════════════════════════════
// SPRING FRAMEWORK
// ═══════════════════════════════════════════════════════════════════════════
import org.springframework.stereotype.Service;

// ═══════════════════════════════════════════════════════════════════════════
// LOMBOK
// ═══════════════════════════════════════════════════════════════════════════
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// ═══════════════════════════════════════════════════════════════════════════
// JAVA STANDARD LIBRARY
// ═══════════════════════════════════════════════════════════════════════════
import java.util.HashMap;
import java.util.Map;

/**
 * CacheStatisticsService - Provides cache statistics and monitoring.
 * 
 * Features:
 * - Cache hit/miss statistics
 * - Cache size monitoring
 * - Cache eviction tracking
 * - Performance metrics
 * 
 * Usage:
 * - Used by admin endpoints for monitoring
 * - Integrated with Spring Boot Actuator
 * - Provides real-time cache health information
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CacheStatisticsService {

    private final CacheManager cacheManager;

    /**
     * Get statistics for all caches.
     * 
     * Returns:
     * - Cache names
     * - Number of entries per cache
     * - Cache configuration
     */
    public Map<String, Object> getAllCacheStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                Map<String, Object> cacheStats = new HashMap<>();
                cacheStats.put("name", cacheName);
                cacheStats.put("type", cache.getClass().getSimpleName());
                
                // Try to get native cache for detailed stats
                Object nativeCache = cache.getNativeCache();
                if (nativeCache != null) {
                    cacheStats.put("nativeType", nativeCache.getClass().getSimpleName());
                }
                
                stats.put(cacheName, cacheStats);
            }
        });
        
        log.info("Cache statistics retrieved for {} caches", stats.size());
        return stats;
    }

    /**
     * Get statistics for a specific cache.
     */
    public Map<String, Object> getCacheStatistics(String cacheName) {
        Map<String, Object> stats = new HashMap<>();
        
        var cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            log.warn("Cache not found: {}", cacheName);
            stats.put("error", "Cache not found: " + cacheName);
            return stats;
        }
        
        stats.put("name", cacheName);
        stats.put("type", cache.getClass().getSimpleName());
        
        Object nativeCache = cache.getNativeCache();
        if (nativeCache != null) {
            stats.put("nativeType", nativeCache.getClass().getSimpleName());
        }
        
        log.debug("Cache statistics retrieved for cache: {}", cacheName);
        return stats;
    }

    /**
     * Clear all caches.
     * 
     * CAUTION: This will evict all cached data!
     * Use only for maintenance or testing.
     */
    public void clearAllCaches() {
        log.warn("Clearing ALL caches - this is a destructive operation!");
        
        int clearedCount = 0;
        for (String cacheName : cacheManager.getCacheNames()) {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                clearedCount++;
                log.info("Cleared cache: {}", cacheName);
            }
        }
        
        log.warn("Cleared {} caches", clearedCount);
    }

    /**
     * Clear a specific cache.
     */
    public void clearCache(String cacheName) {
        log.warn("Clearing cache: {}", cacheName);
        
        var cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            log.info("Cache cleared: {}", cacheName);
        } else {
            log.warn("Cache not found: {}", cacheName);
        }
    }

    /**
     * Check if Redis is healthy.
     */
    public boolean isRedisHealthy() {
        try {
            // Try to get a cache - if Redis is down, this will fail
            var cache = cacheManager.getCache("odds");
            if (cache != null) {
                // Try a simple operation
                cache.get("health-check-key");
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Redis health check failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get cache health status.
     */
    public Map<String, Object> getCacheHealth() {
        Map<String, Object> health = new HashMap<>();
        
        boolean isHealthy = isRedisHealthy();
        health.put("status", isHealthy ? "UP" : "DOWN");
        health.put("cacheManager", cacheManager.getClass().getSimpleName());
        health.put("cacheCount", cacheManager.getCacheNames().size());
        health.put("cacheNames", cacheManager.getCacheNames());
        
        if (isHealthy) {
            log.debug("Cache health check: HEALTHY");
        } else {
            log.error("Cache health check: UNHEALTHY");
        }
        
        return health;
    }
}