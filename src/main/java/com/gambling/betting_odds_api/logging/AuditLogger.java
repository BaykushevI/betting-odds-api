package com.gambling.betting_odds_api.logging;

// SLF4J imports - Standard logging facade
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Spring Framework imports
import org.springframework.stereotype.Component;

// Java standard library imports
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * AuditLogger - Tracks all business operations for compliance and auditing.
 * 
 * ═══════════════════════════════════════════════════════════════════════════
 * WHY AUDIT LOGGING IS CRITICAL IN GAMBLING INDUSTRY:
 * ═══════════════════════════════════════════════════════════════════════════
 * 
 * 1. REGULATORY COMPLIANCE 🏛️
 *    - Gambling regulators (UK Gambling Commission, Malta Gaming Authority, etc.)
 *      REQUIRE detailed audit trails of all operations
 *    - Must prove that odds were not manipulated unfairly
 *    - Fines for non-compliance can be millions of euros
 * 
 * 2. FRAUD DETECTION 🚨
 *    - Detect suspicious patterns (e.g., odds changed 50 times in 5 minutes)
 *    - Identify insider manipulation
 *    - Track who changed what and when
 * 
 * 3. DISPUTE RESOLUTION ⚖️
 *    - Customer claims: "The odds were different when I placed my bet!"
 *    - Audit logs provide irrefutable evidence
 *    - Legal protection for the company
 * 
 * 4. BUSINESS ANALYTICS 📊
 *    - Which sports are most popular?
 *    - How often are odds updated?
 *    - Usage patterns and trends
 * 
 * ═══════════════════════════════════════════════════════════════════════════
 * AUDIT LOG CHARACTERISTICS:
 * ═══════════════════════════════════════════════════════════════════════════
 * 
 * - IMMUTABLE: Never deleted or modified (write-only)
 * - STRUCTURED: Easy to parse and query (consistent format)
 * - COMPREHENSIVE: Contains WHO, WHAT, WHEN, WHERE
 * - LONG-TERM: Stored for years (not days) for compliance
 * - SEARCHABLE: Can be filtered by date, user, operation, etc.
 * 
 * ═══════════════════════════════════════════════════════════════════════════
 */
@Component
public class AuditLogger {
    
    // Special logger that writes to audit.log (configured in logback-spring.xml)
    private static final Logger AUDIT_LOG = LoggerFactory.getLogger("AUDIT");
    
    // Standard timestamp format for consistency
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    // ═══════════════════════════════════════════════════════════════════════
    // CREATE OPERATIONS - Track odds creation
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Log when new betting odds are created.
     * 
     * Example log output:
     * 2025-11-03 14:30:15.123 | AUDIT | CREATE | OddsID=123 | Sport=Football | 
     * Match=Barcelona vs Real Madrid | Timestamp=2025-11-03 14:30:15.123
     * 
     * @param oddsId      Unique ID of created odds
     * @param sport       Sport type (Football, Basketball, etc.)
     * @param homeTeam    Home team name
     * @param awayTeam    Away team name
     */
    public void logOddsCreated(Long oddsId, String sport, String homeTeam, String awayTeam) {
        String message = String.format(
            "CREATE | OddsID=%d | Sport=%s | Match=%s vs %s | Timestamp=%s",
            oddsId, sport, homeTeam, awayTeam, 
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        AUDIT_LOG.info(message);
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // UPDATE OPERATIONS - Track odds modifications
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Log when betting odds are updated.
     * 
     * CRITICAL: We track WHAT changed (the delta), not just that something changed.
     * This is essential for detecting manipulation.
     * 
     * Example log output:
     * 2025-11-03 14:35:20.456 | AUDIT | UPDATE | OddsID=123 | 
     * Match=Barcelona vs Real Madrid | Changes=homeOdds:2.10->2.20 drawOdds:3.40->3.30 | 
     * Timestamp=2025-11-03 14:35:20.456
     * 
     * @param oddsId    Unique ID of updated odds
     * @param homeTeam  Home team name
     * @param awayTeam  Away team name
     * @param changes   String describing what changed (e.g., "homeOdds:2.10->2.20")
     */
    public void logOddsUpdated(Long oddsId, String homeTeam, String awayTeam, String changes) {
        String message = String.format(
            "UPDATE | OddsID=%d | Match=%s vs %s | Changes=%s | Timestamp=%s",
            oddsId, homeTeam, awayTeam, changes, 
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        AUDIT_LOG.info(message);
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // DEACTIVATE OPERATIONS - Track soft deletes (WARN level)
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Log when betting odds are deactivated (soft delete).
     * 
     * Deactivation is significant because:
     * - It removes odds from public view
     * - Might affect pending bets
     * - Could indicate match cancellation or suspicious activity
     * 
     * We use WARN level (not INFO) because deactivation is noteworthy.
     * 
     * Example log output:
     * 2025-11-03 14:40:10.789 | AUDIT | DEACTIVATE | OddsID=123 | 
     * Match=Barcelona vs Real Madrid | Timestamp=2025-11-03 14:40:10.789
     * 
     * @param oddsId    Unique ID of deactivated odds
     * @param homeTeam  Home team name
     * @param awayTeam  Away team name
     */
    public void logOddsDeactivated(Long oddsId, String homeTeam, String awayTeam) {
        String message = String.format(
            "DEACTIVATE | OddsID=%d | Match=%s vs %s | Timestamp=%s",
            oddsId, homeTeam, awayTeam, 
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        AUDIT_LOG.warn(message); // WARN because deactivation is significant
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // DELETE OPERATIONS - Track hard deletes (ERROR level)
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Log when betting odds are permanently deleted (hard delete).
     * 
     * ⚠️ CRITICAL OPERATION ⚠️
     * Hard delete is PERMANENT and IRREVERSIBLE!
     * - Regulatory risk: Lost audit trail
     * - Business risk: Cannot prove what odds were if dispute arises
     * - Should be RARE in production (prefer soft delete)
     * 
     * We use ERROR level (not WARN) to emphasize severity.
     * 
     * Example log output:
     * 2025-11-03 14:45:30.012 | AUDIT | DELETE | OddsID=123 | 
     * Match=Barcelona vs Real Madrid | Timestamp=2025-11-03 14:45:30.012 | ⚠️ HARD DELETE
     * 
     * @param oddsId    Unique ID of deleted odds
     * @param homeTeam  Home team name
     * @param awayTeam  Away team name
     */
    public void logOddsDeleted(Long oddsId, String homeTeam, String awayTeam) {
        String message = String.format(
            "DELETE | OddsID=%d | Match=%s vs %s | Timestamp=%s | ⚠️ HARD DELETE",
            oddsId, homeTeam, awayTeam, 
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        AUDIT_LOG.error(message); // ERROR because hard delete is critical
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // READ OPERATIONS - Track data access
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Log when specific odds are viewed.
     * 
     * Why track reads?
     * - Detect unusual access patterns (e.g., competitor scraping data)
     * - Usage analytics (which matches are most viewed)
     * - Prove data access in case of leaks
     * 
     * Example log output:
     * 2025-11-03 14:50:00.345 | AUDIT | VIEW | OddsID=123 | 
     * Endpoint=/api/odds/123 | Timestamp=2025-11-03 14:50:00.345
     * 
     * @param oddsId    Unique ID of viewed odds
     * @param endpoint  API endpoint used
     */
    public void logOddsViewed(Long oddsId, String endpoint) {
        String message = String.format(
            "VIEW | OddsID=%d | Endpoint=%s | Timestamp=%s",
            oddsId, endpoint, 
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        AUDIT_LOG.info(message);
    }
    
    /**
     * Log bulk query operations (e.g., GET /api/odds with filters).
     * 
     * Example log output:
     * 2025-11-03 14:55:15.678 | AUDIT | QUERY | Endpoint=/api/odds/active | 
     * Records=25 | Filters=active=true | Timestamp=2025-11-03 14:55:15.678
     * 
     * @param endpoint      API endpoint used
     * @param recordCount   Number of records returned
     * @param filters       Applied filters (e.g., "sport=Football, active=true")
     */
    public void logBulkQuery(String endpoint, int recordCount, String filters) {
        String message = String.format(
            "QUERY | Endpoint=%s | Records=%d | Filters=%s | Timestamp=%s",
            endpoint, recordCount, filters, 
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        AUDIT_LOG.info(message);
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // BUSINESS LOGIC - Track calculations
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Log bookmaker margin calculations.
     * 
     * Why track calculations?
     * - Prove that margins were calculated correctly
     * - Detect anomalies (e.g., negative margins = data error)
     * - Business intelligence (average margins per sport)
     * 
     * Example log output:
     * 2025-11-03 15:00:45.901 | AUDIT | CALCULATION | Type=BookmakerMargin | 
     * OddsID=123 | Match=Barcelona vs Real Madrid | Margin=4.80% | 
     * Timestamp=2025-11-03 15:00:45.901
     * 
     * @param oddsId    Unique ID of odds
     * @param margin    Calculated margin percentage
     * @param homeTeam  Home team name
     * @param awayTeam  Away team name
     */
    public void logMarginCalculation(Long oddsId, double margin, 
                                       String homeTeam, String awayTeam) {
        String message = String.format(
            "CALCULATION | Type=BookmakerMargin | OddsID=%d | Match=%s vs %s | " +
            "Margin=%.2f%% | Timestamp=%s",
            oddsId, homeTeam, awayTeam, margin, 
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        AUDIT_LOG.info(message);
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // VALIDATION FAILURES - Track suspicious input
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Log validation failures.
     * 
     * Why track validation failures?
     * - Detect malicious input attempts (e.g., SQL injection)
     * - Identify buggy clients
     * - Improve validation rules based on real failures
     * 
     * Example log output:
     * 2025-11-03 15:05:20.234 | AUDIT | VALIDATION_FAILED | 
     * Endpoint=/api/odds | Errors=homeOdds:must be >= 1.01, sport:too short | 
     * Timestamp=2025-11-03 15:05:20.234
     * 
     * @param endpoint      API endpoint
     * @param fieldErrors   Validation errors (e.g., "homeOdds:too low, sport:too short")
     */
    public void logValidationFailure(String endpoint, String fieldErrors) {
        String message = String.format(
            "VALIDATION_FAILED | Endpoint=%s | Errors=%s | Timestamp=%s",
            endpoint, fieldErrors, 
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        AUDIT_LOG.warn(message);
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // CUSTOM EVENTS - Generic audit logging
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Log custom audit event.
     * 
     * Use this for any business event not covered by specific methods.
     * 
     * Example log output:
     * 2025-11-03 15:10:00.567 | AUDIT | SYSTEM_STARTUP | 
     * Details=Application started successfully | Timestamp=2025-11-03 15:10:00.567
     * 
     * @param eventType  Type of event (e.g., "SYSTEM_STARTUP", "CACHE_CLEARED")
     * @param details    Event details
     */
    public void logCustomEvent(String eventType, String details) {
        String message = String.format(
            "%s | Details=%s | Timestamp=%s",
            eventType, details, 
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        AUDIT_LOG.info(message);
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // FUTURE ENHANCEMENTS (Phase 3: Security & Authentication)
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Future: When you add user authentication, enhance audit logs with user info.
     * 
     * Example:
     * public void logOddsCreatedByUser(Long oddsId, String userId, String username,
     *                                   String sport, String homeTeam, String awayTeam) {
     *     String message = String.format(
     *         "CREATE | OddsID=%d | UserID=%s | Username=%s | Sport=%s | " +
     *         "Match=%s vs %s | Timestamp=%s",
     *         oddsId, userId, username, sport, homeTeam, awayTeam,
     *         LocalDateTime.now().format(TIMESTAMP_FORMATTER)
     *     );
     *     AUDIT_LOG.info(message);
     * }
     * 
     * This will provide:
     * - WHO created/modified/deleted the odds (accountability)
     * - User behavior patterns (for fraud detection)
     * - Compliance evidence (regulatory requirement)
     */
}