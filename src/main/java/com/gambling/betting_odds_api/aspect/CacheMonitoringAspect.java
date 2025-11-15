package com.gambling.betting_odds_api.aspect;

// ═══════════════════════════════════════════════════════════════════════════
// ASPECTJ - AOP support
// ═══════════════════════════════════════════════════════════════════════════
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

// ═══════════════════════════════════════════════════════════════════════════
// SPRING FRAMEWORK
// ═══════════════════════════════════════════════════════════════════════════
import org.springframework.stereotype.Component;

// ═══════════════════════════════════════════════════════════════════════════
// INTERNAL PROJECT IMPORTS
// ═══════════════════════════════════════════════════════════════════════════
import com.gambling.betting_odds_api.logging.PerformanceLogger;

// ═══════════════════════════════════════════════════════════════════════════
// LOMBOK
// ═══════════════════════════════════════════════════════════════════════════
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * CacheMonitoringAspect - Automatically monitors all @Cacheable methods.
 * 
 * This aspect intercepts methods annotated with:
 * - @Cacheable
 * - @CachePut
 * - @CacheEvict
 * 
 * And logs:
 * - Method name
 * - Execution time
 * - Cache hit/miss (for @Cacheable)
 * 
 * This is optional but very useful for production monitoring!
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class CacheMonitoringAspect {

    private final PerformanceLogger performanceLogger;

    /**
     * Monitor @Cacheable methods.
     * 
     * Logs execution time and whether result came from cache or DB.
     */
    @Around("@annotation(org.springframework.cache.annotation.Cacheable)")
    public Object monitorCacheable(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        long startTime = System.currentTimeMillis();
        
        log.debug("[CACHE MONITOR] Executing @Cacheable method: {}", methodName);
        
        Object result = joinPoint.proceed();
        
        long duration = System.currentTimeMillis() - startTime;
        
        // If very fast (<10ms), likely from cache. If slow (>100ms), likely from DB.
        boolean likelyCacheHit = duration < 50;
        
        performanceLogger.logCacheOperation(
            "GET", 
            methodName, 
            duration, 
            likelyCacheHit
        );
        
        return result;
    }

    /**
     * Monitor @CachePut methods.
     */
    @Around("@annotation(org.springframework.cache.annotation.CachePut)")
    public Object monitorCachePut(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        long startTime = System.currentTimeMillis();
        
        log.debug("[CACHE MONITOR] Executing @CachePut method: {}", methodName);
        
        Object result = joinPoint.proceed();
        
        long duration = System.currentTimeMillis() - startTime;
        
        performanceLogger.logCacheOperation(
            "PUT", 
            methodName, 
            duration, 
            false  // Always false for PUT (always updates cache)
        );
        
        return result;
    }

    /**
     * Monitor @CacheEvict methods.
     */
    @Around("@annotation(org.springframework.cache.annotation.CacheEvict)")
    public Object monitorCacheEvict(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        long startTime = System.currentTimeMillis();
        
        log.debug("[CACHE MONITOR] Executing @CacheEvict method: {}", methodName);
        
        Object result = joinPoint.proceed();
        
        long duration = System.currentTimeMillis() - startTime;
        
        performanceLogger.logCacheOperation(
            "EVICT", 
            methodName, 
            duration, 
            false  // Always false for EVICT
        );
        
        return result;
    }
}