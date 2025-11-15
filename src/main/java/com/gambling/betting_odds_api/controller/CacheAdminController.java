package com.gambling.betting_odds_api.controller;

// ═══════════════════════════════════════════════════════════════════════════
// INTERNAL PROJECT IMPORTS
// ═══════════════════════════════════════════════════════════════════════════
import com.gambling.betting_odds_api.service.CacheStatisticsService;

// ═══════════════════════════════════════════════════════════════════════════
// SPRING WEB - REST API support
// ═══════════════════════════════════════════════════════════════════════════
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// ═══════════════════════════════════════════════════════════════════════════
// LOMBOK
// ═══════════════════════════════════════════════════════════════════════════
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// ═══════════════════════════════════════════════════════════════════════════
// JAVA STANDARD LIBRARY
// ═══════════════════════════════════════════════════════════════════════════
import java.util.Map;

/**
 * CacheAdminController - Admin endpoints for cache management.
 * 
 * Security:
 * - All endpoints require ADMIN role
 * - Use with caution - cache clearing affects all users!
 * 
 * Endpoints:
 * - GET  /api/admin/cache/stats - Get cache statistics
 * - GET  /api/admin/cache/health - Check cache health
 * - POST /api/admin/cache/clear - Clear all caches
 * - POST /api/admin/cache/{cacheName}/clear - Clear specific cache
 */
@RestController
@RequestMapping("/api/admin/cache")
@RequiredArgsConstructor
@Slf4j
public class CacheAdminController {

    private final CacheStatisticsService cacheStatisticsService;

    /**
     * Get statistics for all caches.
     * 
     * Returns cache names, types, and configuration.
     * 
     * Example:
     * GET /api/admin/cache/stats
     * 
     * Response:
     * {
     *   "odds": {
     *     "name": "odds",
     *     "type": "RedisCache",
     *     "nativeType": "RedisCache"
     *   },
     *   "odds-all": { ... }
     * }
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getCacheStatistics() {
        log.info("Admin request: Get cache statistics");
        Map<String, Object> stats = cacheStatisticsService.getAllCacheStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * Get statistics for a specific cache.
     * 
     * Example:
     * GET /api/admin/cache/odds/stats
     */
    @GetMapping("/{cacheName}/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getCacheStatistics(@PathVariable String cacheName) {
        log.info("Admin request: Get statistics for cache: {}", cacheName);
        Map<String, Object> stats = cacheStatisticsService.getCacheStatistics(cacheName);
        return ResponseEntity.ok(stats);
    }

    /**
     * Check cache health.
     * 
     * Returns cache status and Redis connectivity.
     * 
     * Example:
     * GET /api/admin/cache/health
     * 
     * Response:
     * {
     *   "status": "UP",
     *   "cacheManager": "RedisCacheManager",
     *   "cacheCount": 6,
     *   "cacheNames": ["odds", "odds-all", ...]
     * }
     */
    @GetMapping("/health")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getCacheHealth() {
        log.info("Admin request: Check cache health");
        Map<String, Object> health = cacheStatisticsService.getCacheHealth();
        return ResponseEntity.ok(health);
    }

    /**
     * Clear all caches.
     * 
     * CAUTION: This will evict all cached data!
     * All subsequent requests will query the database until cache rebuilds.
     * 
     * Example:
     * POST /api/admin/cache/clear
     * 
     * Response:
     * {
     *   "message": "All caches cleared successfully"
     * }
     */
    @PostMapping("/clear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> clearAllCaches() {
        log.warn("Admin request: Clear ALL caches");
        cacheStatisticsService.clearAllCaches();
        return ResponseEntity.ok(Map.of(
            "message", "All caches cleared successfully",
            "warning", "All subsequent requests will query database until cache rebuilds"
        ));
    }

    /**
     * Clear a specific cache.
     * 
     * Example:
     * POST /api/admin/cache/odds/clear
     * 
     * Response:
     * {
     *   "message": "Cache 'odds' cleared successfully"
     * }
     */
    @PostMapping("/{cacheName}/clear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> clearCache(@PathVariable String cacheName) {
        log.warn("Admin request: Clear cache: {}", cacheName);
        cacheStatisticsService.clearCache(cacheName);
        return ResponseEntity.ok(Map.of(
            "message", String.format("Cache '%s' cleared successfully", cacheName)
        ));
    }
}