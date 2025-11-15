package com.gambling.betting_odds_api.config;

// ═══════════════════════════════════════════════════════════════════════════
// SPRING CACHE - Cache event support
// ═══════════════════════════════════════════════════════════════════════════
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;

// ═══════════════════════════════════════════════════════════════════════════
// SPRING FRAMEWORK
// ═══════════════════════════════════════════════════════════════════════════
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

// ═══════════════════════════════════════════════════════════════════════════
// LOMBOK
// ═══════════════════════════════════════════════════════════════════════════
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * CacheEventLogger - Logs cache operations for monitoring and debugging.
 * 
 * Features:
 * - Logs cache hits/misses
 * - Logs cache evictions
 * - Handles cache errors gracefully
 * - Integrates with existing logging system
 * 
 * This class implements CachingConfigurer to customize Spring Cache behavior.
 * By implementing errorHandler(), we can log cache errors without breaking the app.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class CacheEventLogger implements CachingConfigurer {

    /**
     * Custom error handler for cache operations.
     * 
     * When cache operations fail (e.g., Redis connection lost),
     * this handler logs the error but allows the app to continue
     * by falling back to database queries.
     * 
     * This is critical for fault tolerance!
     */
    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                log.error("[CACHE ERROR] Failed to GET from cache '{}' with key '{}': {}", 
                    cache.getName(), key, exception.getMessage());
                log.debug("Stack trace:", exception);
                // Don't throw exception - allow fallback to database
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, @Nullable Object value) {
                log.error("[CACHE ERROR] Failed to PUT into cache '{}' with key '{}': {}", 
                    cache.getName(), key, exception.getMessage());
                log.debug("Stack trace:", exception);
                // Don't throw exception - data still saved to database
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                log.error("[CACHE ERROR] Failed to EVICT from cache '{}' with key '{}': {}", 
                    cache.getName(), key, exception.getMessage());
                log.debug("Stack trace:", exception);
                // Don't throw exception - data still deleted from database
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                log.error("[CACHE ERROR] Failed to CLEAR cache '{}': {}", 
                    cache.getName(), exception.getMessage());
                log.debug("Stack trace:", exception);
                // Don't throw exception - operation continues
            }
        };
    }

    // We can also override other CachingConfigurer methods if needed:
    
    @Override
    @Nullable
    public CacheManager cacheManager() {
        // Return null to use default CacheManager (RedisCacheManager)
        return null;
    }

    @Override
    @Nullable
    public CacheResolver cacheResolver() {
        // Return null to use default CacheResolver
        return null;
    }

    @Override
    @Nullable
    public KeyGenerator keyGenerator() {
        // Return null to use default KeyGenerator
        // (SpEL expressions in @Cacheable key attribute)
        return null;
    }
}