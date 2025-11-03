package com.gambling.betting_odds_api.logging;

// ═══════════════════════════════════════════════════════════════════════════
// SLF4J IMPORTS - Standard logging facade
// ═══════════════════════════════════════════════════════════════════════════
import org.slf4j.Logger;           // Logger interface
import org.slf4j.LoggerFactory;    // Logger factory for creating loggers

// ═══════════════════════════════════════════════════════════════════════════
// SPRING FRAMEWORK IMPORTS - Dependency injection
// ═══════════════════════════════════════════════════════════════════════════
import org.springframework.stereotype.Component; // Marks class as Spring component

// ═══════════════════════════════════════════════════════════════════════════
// JAVA STANDARD LIBRARY - Core Java classes
// ═══════════════════════════════════════════════════════════════════════════
import java.time.LocalDateTime;                 // Date/time operations
import java.time.format.DateTimeFormatter;      // Date formatting

/**
 * PerformanceLogger - Track method execution times and identify bottlenecks.
 * 
 * ═══════════════════════════════════════════════════════════════════════════
 * WHY PERFORMANCE LOGGING IS CRITICAL:
 * ═══════════════════════════════════════════════════════════════════════════
 * 
 * 1. IDENTIFY SLOW OPERATIONS 🐌
 *    - Which database queries take > 1 second?
 *    - Which API endpoints are slow?
 *    - Which calculations are bottlenecks?
 * 
 * 2. DETECT PERFORMANCE DEGRADATION 📉
 *    - Was this operation fast yesterday but slow today?
 *    - Is performance getting worse over time?
 *    - Did a code change make things slower?
 * 
 * 3. OPTIMIZE BASED ON DATA 📊
 *    - Don't guess - measure!
 *    - Focus optimization efforts on actual bottlenecks
 *    - Prove that optimizations worked
 * 
 * 4. PRODUCTION MONITORING ⚠️
 *    - Alert when operations exceed thresholds
 *    - Example: "Database query took 5 seconds - investigate!"
 *    - Catch issues before users complain
 * 
 * ═══════════════════════════════════════════════════════════════════════════
 * HOW TO USE:
 * ═══════════════════════════════════════════════════════════════════════════
 * 
 * Pattern:
 * 1. Start timing:  long startTime = PerformanceLogger.startTiming();
 * 2. Do operation:  // your code here
 * 3. End timing:    long duration = PerformanceLogger.endTiming(startTime);
 * 4. Log result:    performanceLogger.logDatabaseQuery("CREATE_ODDS", duration);
 * 
 * Example in Service:
 * 
 * public OddsResponse createOdds(CreateOddsRequest request) {
 *     long startTime = PerformanceLogger.startTiming();
 *     
 *     // Business logic here
 *     BettingOdds saved = repository.save(odds);
 *     
 *     long duration = PerformanceLogger.endTiming(startTime);
 *     performanceLogger.logDatabaseQuery("CREATE_ODDS", duration);
 *     
 *     return mapper.toResponse(saved);
 * }
 * 
 * ═══════════════════════════════════════════════════════════════════════════
 */
@Component
public class PerformanceLogger {
    
    // Special logger that writes to performance.log (configured in logback-spring.xml)
    private static final Logger PERF_LOG = LoggerFactory.getLogger("PERFORMANCE");
    
    // Standard timestamp format for consistency
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    // ═══════════════════════════════════════════════════════════════════════
    // PERFORMANCE THRESHOLDS - What is considered "slow"?
    // ═══════════════════════════════════════════════════════════════════════
    // These values are industry standards for acceptable performance
    
    // Database queries
    // - < 100ms = Excellent
    // - 100-500ms = Good
    // - 500-1000ms = Acceptable
    // - > 1000ms = SLOW (investigate!)
    private static final long SLOW_DB_QUERY_THRESHOLD = 1000;  // 1 second
    
    // API endpoints (total request processing time)
    // - < 200ms = Excellent (user doesn't notice)
    // - 200-1000ms = Good (acceptable)
    // - 1000-2000ms = Noticeable delay
    // - > 2000ms = SLOW (user frustration!)
    private static final long SLOW_API_CALL_THRESHOLD = 2000;  // 2 seconds
    
    // Business calculations (e.g., margin calculation)
    // - < 50ms = Excellent
    // - 50-200ms = Good
    // - 200-500ms = Acceptable
    // - > 500ms = SLOW (for a simple calculation!)
    private static final long SLOW_CALCULATION_THRESHOLD = 500; // 500ms
    
    // ═══════════════════════════════════════════════════════════════════════
    // DATABASE QUERY PERFORMANCE
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Log database query performance.
     * 
     * Automatically determines if query is SLOW or NORMAL based on threshold.
     * 
     * Example log output (NORMAL):
     * 2025-11-03 14:30:15.123 | PERF | DB_QUERY | Type=CREATE_ODDS | 
     * ExecutionTime=250ms | Level=NORMAL | Timestamp=2025-11-03 14:30:15.123
     * 
     * Example log output (SLOW):
     * 2025-11-03 14:30:15.123 | PERF | DB_QUERY | Type=GET_ALL_ODDS | 
     * ExecutionTime=1500ms | Level=SLOW | Timestamp=2025-11-03 14:30:15.123
     * 
     * @param queryType Description of query (e.g., "CREATE_ODDS", "GET_ALL_ODDS")
     * @param executionTimeMs Execution time in milliseconds
     */
    public void logDatabaseQuery(String queryType, long executionTimeMs) {
        // Determine if slow
        String level = executionTimeMs > SLOW_DB_QUERY_THRESHOLD ? "SLOW" : "NORMAL";
        
        String message = String.format(
            "DB_QUERY | Type=%s | ExecutionTime=%dms | Level=%s | Timestamp=%s",
            queryType, executionTimeMs, level, 
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        
        // Use WARN level for slow queries (they need attention!)
        // Use INFO level for normal queries
        if (executionTimeMs > SLOW_DB_QUERY_THRESHOLD) {
            PERF_LOG.warn(message); // Slow queries = WARN
        } else {
            PERF_LOG.info(message); // Normal queries = INFO
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // API ENDPOINT PERFORMANCE
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Log API endpoint performance.
     * 
     * Tracks total request processing time from controller entry to response.
     * 
     * Example log output:
     * 2025-11-03 14:35:20.456 | PERF | API_CALL | Method=POST | 
     * Endpoint=/api/odds | ExecutionTime=350ms | Status=201 | Level=NORMAL | 
     * Timestamp=2025-11-03 14:35:20.456
     * 
     * @param method HTTP method (GET, POST, PUT, DELETE)
     * @param endpoint API endpoint path
     * @param executionTimeMs Execution time in milliseconds
     * @param statusCode HTTP status code (200, 201, 404, etc.)
     */
    public void logApiEndpoint(String method, String endpoint, 
                                long executionTimeMs, int statusCode) {
        String level = executionTimeMs > SLOW_API_CALL_THRESHOLD ? "SLOW" : "NORMAL";
        
        String message = String.format(
            "API_CALL | Method=%s | Endpoint=%s | ExecutionTime=%dms | Status=%d | " +
            "Level=%s | Timestamp=%s",
            method, endpoint, executionTimeMs, statusCode, level,
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        
        if (executionTimeMs > SLOW_API_CALL_THRESHOLD) {
            PERF_LOG.warn(message); // Slow API calls = WARN
        } else {
            PERF_LOG.debug(message); // Normal API calls = DEBUG (less noise in production)
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // BUSINESS LOGIC CALCULATIONS
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Log business logic calculation performance.
     * 
     * Used for tracking performance of calculations like:
     * - Bookmaker margin calculation
     * - Implied probability calculation
     * - Complex data transformations
     * 
     * Example log output:
     * 2025-11-03 14:40:10.789 | PERF | CALCULATION | Type=BookmakerMargin | 
     * ExecutionTime=45ms | Level=NORMAL | Timestamp=2025-11-03 14:40:10.789
     * 
     * @param calculationType Description of calculation
     * @param executionTimeMs Execution time in milliseconds
     */
    public void logCalculation(String calculationType, long executionTimeMs) {
        String level = executionTimeMs > SLOW_CALCULATION_THRESHOLD ? "SLOW" : "NORMAL";
        
        String message = String.format(
            "CALCULATION | Type=%s | ExecutionTime=%dms | Level=%s | Timestamp=%s",
            calculationType, executionTimeMs, level,
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        
        if (executionTimeMs > SLOW_CALCULATION_THRESHOLD) {
            PERF_LOG.warn(message); // Slow calculations = WARN
        } else {
            PERF_LOG.info(message); // Normal calculations = INFO
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // PAGINATION PERFORMANCE
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Log pagination performance.
     * 
     * Important for large datasets - pagination can become slow as data grows.
     * 
     * Red flags:
     * - Getting page 1 is fast, but page 100 is slow (OFFSET problem!)
     * - Large page sizes cause memory issues
     * - Too many total records (need better filtering)
     * 
     * Example log output:
     * 2025-11-03 14:45:30.012 | PERF | PAGINATION | Endpoint=/api/odds | 
     * Page=0 | Size=20 | Total=1500 | ExecutionTime=450ms | 
     * Timestamp=2025-11-03 14:45:30.012
     * 
     * @param endpoint API endpoint
     * @param pageNumber Current page (0-indexed)
     * @param pageSize Items per page
     * @param totalRecords Total records in database
     * @param executionTimeMs Execution time in milliseconds
     */
    public void logPagination(String endpoint, int pageNumber, int pageSize, 
                               int totalRecords, long executionTimeMs) {
        String message = String.format(
            "PAGINATION | Endpoint=%s | Page=%d | Size=%d | Total=%d | " +
            "ExecutionTime=%dms | Timestamp=%s",
            endpoint, pageNumber, pageSize, totalRecords, executionTimeMs,
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        
        // Pagination is considered slow if > 1 second (database query threshold)
        if (executionTimeMs > SLOW_DB_QUERY_THRESHOLD) {
            PERF_LOG.warn(message); // Slow pagination = WARN
        } else {
            PERF_LOG.info(message); // Normal pagination = INFO
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // TIMING HELPER METHODS
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Start timing an operation.
     * 
     * Returns current time in milliseconds.
     * Use with endTiming() to calculate duration.
     * 
     * Usage:
     * long startTime = PerformanceLogger.startTiming();
     * // ... do something ...
     * long duration = PerformanceLogger.endTiming(startTime);
     * 
     * @return Start time in milliseconds
     */
    public static long startTiming() {
        return System.currentTimeMillis();
    }
    
    /**
     * End timing an operation.
     * 
     * Calculates duration since startTiming() was called.
     * 
     * @param startTime Start time from startTiming()
     * @return Duration in milliseconds
     */
    public static long endTiming(long startTime) {
        return System.currentTimeMillis() - startTime;
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // MEMORY USAGE MONITORING
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Log memory usage.
     * 
     * Useful for detecting memory leaks or high memory operations.
     * 
     * Example log output:
     * 2025-11-03 14:50:00.345 | PERF | MEMORY | Operation=GetAllOdds | 
     * Used=512MB | Free=256MB | Total=768MB | Timestamp=2025-11-03 14:50:00.345
     * 
     * When to use:
     * - After processing large datasets
     * - After bulk operations
     * - When investigating memory issues
     * 
     * @param operation Description of operation
     */
    public void logMemoryUsage(String operation) {
        Runtime runtime = Runtime.getRuntime();
        
        // Convert bytes to megabytes for readability
        long totalMemory = runtime.totalMemory() / (1024 * 1024); // MB
        long freeMemory = runtime.freeMemory() / (1024 * 1024);   // MB
        long usedMemory = totalMemory - freeMemory;               // MB
        
        String message = String.format(
            "MEMORY | Operation=%s | Used=%dMB | Free=%dMB | Total=%dMB | Timestamp=%s",
            operation, usedMemory, freeMemory, totalMemory,
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        
        PERF_LOG.info(message);
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // FUTURE ENHANCEMENTS
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Future improvements for Phase 4 (Performance & Reliability):
     * 
     * 1. PERCENTILE TRACKING
     *    - Track not just average, but p50, p95, p99
     *    - Example: "95% of queries complete in < 200ms"
     * 
     * 2. TREND ANALYSIS
     *    - Compare current performance to historical average
     *    - Example: "This query is 3x slower than usual!"
     * 
     * 3. PROMETHEUS METRICS
     *    - Export metrics in Prometheus format
     *    - Integrate with Grafana dashboards
     * 
     * 4. AUTOMATIC ALERTS
     *    - Send notification when thresholds exceeded
     *    - Example: Email when query > 5 seconds
     * 
     * 5. CORRELATION WITH ERRORS
     *    - Link slow operations with error rates
     *    - Example: "Slow queries cause timeouts"
     */
}