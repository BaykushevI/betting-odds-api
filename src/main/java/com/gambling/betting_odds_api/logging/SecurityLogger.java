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
 * SecurityLogger - Track security-related events and suspicious activity.
 * 
 * ═══════════════════════════════════════════════════════════════════════════
 * WHY SECURITY LOGGING IS CRITICAL IN GAMBLING INDUSTRY:
 * ═══════════════════════════════════════════════════════════════════════════
 * 
 * 1. FRAUD DETECTION 
 *    - Detect suspicious odds manipulation
 *    - Identify unusual access patterns
 *    - Track repeated validation failures (could be attack)
 *    - Monitor for data scraping attempts
 * 
 * 2. REGULATORY COMPLIANCE 
 *    - Gambling regulators require security logs
 *    - Prove you're detecting and preventing fraud
 *    - Evidence for investigations
 *    - Fines for inadequate security measures
 * 
 * 3. ATTACK PREVENTION 
 *    - SQL injection attempts
 *    - XSS (Cross-Site Scripting) attempts
 *    - Rate limiting violations (DoS attacks)
 *    - Unauthorized access attempts
 * 
 * 4. INSIDER THREAT DETECTION 
 *    - Track who changes odds (when we add auth in Phase 3)
 *    - Detect unusual admin behavior
 *    - Monitor privileged operations
 * 
 * ═══════════════════════════════════════════════════════════════════════════
 * WHAT TO LOG:
 * ═══════════════════════════════════════════════════════════════════════════
 * 
 * - Invalid data attempts (suspicious odds values)
 * - Repeated validation failures (possible attack)
 * - Unusual odds changes (manipulation attempts)
 * - Excessive API calls (scraping or DoS)
 * - SQL injection patterns in input
 * - XSS patterns in input
 * 
 * Future (Phase 3 - Security & Authentication):
 * - Failed login attempts
 * - Successful logins (with IP tracking)
 * - Unauthorized access attempts
 * - Privilege escalation attempts
 * 
 * ═══════════════════════════════════════════════════════════════════════════
 */
@Component
public class SecurityLogger {
    
    // Special logger that writes to security.log (configured in logback-spring.xml)
    private static final Logger SECURITY_LOG = LoggerFactory.getLogger("SECURITY");
    
    // Standard timestamp format for consistency
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    // ═══════════════════════════════════════════════════════════════════════
    // ODDS MANIPULATION DETECTION
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Log suspicious odds changes.
     * 
     * What makes an odds change suspicious?
     * - Change > 50% (e.g., 2.00 → 3.50)
     * - Multiple rapid changes (> 10 changes in 1 minute)
     * - Change just before match starts (last-minute manipulation)
     * - Odds that don't make mathematical sense
     * 
     * Example log output:
     * 2025-11-03 14:30:15.123 | SECURITY | SUSPICIOUS_ODDS_CHANGE | 
     * OddsID=123 | Old=2.10 | New=3.50 | Reason=Changed by more than 50% | 
     * Timestamp=2025-11-03 14:30:15.123 | ALERT
     * 
     * @param oddsId Unique ID of odds
     * @param oldValue Old odds value (as string for flexibility)
     * @param newValue New odds value
     * @param reason Why this is suspicious
     */
    public void logSuspiciousOddsChange(Long oddsId, String oldValue, 
                                         String newValue, String reason) {
        String message = String.format(
            "SUSPICIOUS_ODDS_CHANGE | OddsID=%d | Old=%s | New=%s | Reason=%s | " +
            "Timestamp=%s | ALERT",
            oddsId, oldValue, newValue, reason,
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        
        // WARN level - suspicious but not confirmed attack
        SECURITY_LOG.warn(message);
    }
    
    /**
     * Log invalid odds values.
     * 
     * Invalid odds might indicate:
     * - Typo (human error)
     * - Bug in client software
     * - Malicious attempt to manipulate system
     * 
     * Invalid patterns:
     * - Odds < 1.01 (impossible - guarantees loss for bookmaker)
     * - Odds > 1000 (unrealistic)
     * - Negative odds
     * - Non-numeric values
     * 
     * Example log output:
     * 2025-11-03 14:35:20.456 | SECURITY | INVALID_ODDS_ATTEMPT | 
     * Match=Barcelona vs Real Madrid | HomeOdds=0.50 | DrawOdds=3.40 | 
     * AwayOdds=3.60 | Reason=Home odds below minimum (1.01) | 
     * Timestamp=2025-11-03 14:35:20.456
     * 
     * @param homeTeam Home team name
     * @param awayTeam Away team name
     * @param homeOdds Home odds value
     * @param drawOdds Draw odds value
     * @param awayOdds Away odds value
     * @param reason Why these odds are invalid
     */
    public void logInvalidOdds(String homeTeam, String awayTeam, 
                                double homeOdds, double drawOdds, double awayOdds, 
                                String reason) {
        String message = String.format(
            "INVALID_ODDS_ATTEMPT | Match=%s vs %s | HomeOdds=%.2f | " +
            "DrawOdds=%.2f | AwayOdds=%.2f | Reason=%s | Timestamp=%s",
            homeTeam, awayTeam, homeOdds, drawOdds, awayOdds, reason,
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        
        // ERROR level - this is a serious validation failure
        SECURITY_LOG.error(message);
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // ATTACK DETECTION
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Log excessive API calls (potential DoS or scraping).
     * 
     * Indicators of abuse:
     * - Same IP makes > 100 requests per minute
     * - Same user makes > 1000 requests per hour
     * - Automated patterns (exact timing between requests)
     * - Requests for all data (scraping attempt)
     * 
     * Example log output:
     * 2025-11-03 14:40:10.789 | SECURITY | EXCESSIVE_API_CALLS | 
     * IP=192.168.1.100 | Endpoint=/api/odds | Calls=150 | Threshold=100 | 
     * Timestamp=2025-11-03 14:40:10.789 | RATE_LIMIT
     * 
     * @param ipAddress Client IP address
     * @param endpoint API endpoint being called
     * @param callCount Number of calls in time window
     * @param threshold Maximum allowed calls
     */
    public void logExcessiveApiCalls(String ipAddress, String endpoint, 
                                      int callCount, int threshold) {
        String message = String.format(
            "EXCESSIVE_API_CALLS | IP=%s | Endpoint=%s | Calls=%d | " +
            "Threshold=%d | Timestamp=%s | RATE_LIMIT",
            ipAddress, endpoint, callCount, threshold,
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        
        // WARN level - might be legitimate high traffic, might be attack
        SECURITY_LOG.warn(message);
    }
    
    /**
     * Log unauthorized access attempts.
     * 
     * Future (Phase 3 - Security):
     * - User tries to access admin endpoint without permission
     * - User tries to delete odds without DELETE privilege
     * - Expired JWT token
     * 
     * Example log output:
     * 2025-11-03 14:45:30.012 | SECURITY | UNAUTHORIZED_ACCESS | 
     * Resource=/api/odds/123 | Action=DELETE | 
     * Timestamp=2025-11-03 14:45:30.012 | SECURITY_BREACH
     * 
     * @param resource Resource being accessed
     * @param attemptedAction Action attempted (READ, WRITE, DELETE)
     */
    public void logUnauthorizedAccess(String resource, String attemptedAction) {
        String message = String.format(
            "UNAUTHORIZED_ACCESS | Resource=%s | Action=%s | " +
            "Timestamp=%s | SECURITY_BREACH",
            resource, attemptedAction,
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        
        // ERROR level - unauthorized access is serious
        SECURITY_LOG.error(message);
    }
    
    /**
     * Log SQL injection attempts.
     * 
     * Detected patterns:
     * - ' OR '1'='1
     * - '; DROP TABLE --
     * - UNION SELECT
     * - <script> tags
     * - ../../../etc/passwd
     * 
     * Example log output:
     * 2025-11-03 14:50:00.345 | SECURITY | SQL_INJECTION_ATTEMPT | 
     * Endpoint=/api/odds/sport/Football' OR '1'='1 | 
     * Input=Football' OR '1'='1 | Timestamp=2025-11-03 14:50:00.345 | ATTACK
     * 
     * @param endpoint API endpoint
     * @param suspiciousInput Input containing SQL injection pattern
     */
    public void logSqlInjectionAttempt(String endpoint, String suspiciousInput) {
        String message = String.format(
            "SQL_INJECTION_ATTEMPT | Endpoint=%s | Input=%s | " +
            "Timestamp=%s | ATTACK",
            endpoint, sanitizeForLog(suspiciousInput),
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        
        // ERROR level - this is an attack
        SECURITY_LOG.error(message);
    }
    
    /**
     * Log XSS (Cross-Site Scripting) attempts.
     * 
     * Detected patterns:
     * - <script>alert('XSS')</script>
     * - <img src=x onerror="alert('XSS')">
     * - javascript:alert('XSS')
     * - <iframe> tags
     * 
     * Example log output:
     * 2025-11-03 14:55:15.678 | SECURITY | XSS_ATTEMPT | 
     * Endpoint=/api/odds | Input=<script>alert('XSS')</script> | 
     * Timestamp=2025-11-03 14:55:15.678 | ATTACK
     * 
     * @param endpoint API endpoint
     * @param suspiciousInput Input containing XSS pattern
     */
    public void logXssAttempt(String endpoint, String suspiciousInput) {
        String message = String.format(
            "XSS_ATTEMPT | Endpoint=%s | Input=%s | Timestamp=%s | 🚨 ATTACK",
            endpoint, sanitizeForLog(suspiciousInput),
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        
        // ERROR level - this is an attack
        SECURITY_LOG.error(message);
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // DATA INTEGRITY THREATS
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Log unusual deletion patterns.
     * 
     * Suspicious deletion patterns:
     * - Mass deletion (> 100 records in short time)
     * - Deletion of historical data (should be immutable)
     * - Deletion outside business hours
     * 
     * Example log output:
     * 2025-11-03 15:00:45.901 | SECURITY | MASS_DELETION | 
     * Records=250 | TimeWindow=5 minutes | 
     * Timestamp=2025-11-03 15:00:45.901 | SUSPICIOUS
     * 
     * @param recordCount Number of records deleted
     * @param timeWindow Time period (e.g., "5 minutes", "1 hour")
     */
    public void logMassDeletion(int recordCount, String timeWindow) {
        String message = String.format(
            "MASS_DELETION | Records=%d | TimeWindow=%s | " +
            "Timestamp=%s | SUSPICIOUS",
            recordCount, timeWindow,
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        
        // ERROR level - mass deletion is very suspicious
        SECURITY_LOG.error(message);
    }
    
    /**
     * Log changes to historical data.
     * 
     * In gambling, historical odds should be IMMUTABLE.
     * Changing past odds is:
     * - Potentially fraudulent
     * - Violates regulatory requirements
     * - Could hide evidence of manipulation
     * 
     * Example log output:
     * 2025-11-03 15:05:20.234 | SECURITY | HISTORICAL_DATA_CHANGE | 
     * OddsID=123 | MatchDate=2025-10-15 (past) | 
     * Reason=Match already occurred | 
     * Timestamp=2025-11-03 15:05:20.234 | ALERT
     * 
     * @param oddsId Unique ID of odds
     * @param matchDate Date of match
     * @param reason Why this is suspicious
     */
    public void logHistoricalDataChange(Long oddsId, String matchDate, String reason) {
        String message = String.format(
            "HISTORICAL_DATA_CHANGE | OddsID=%d | MatchDate=%s | Reason=%s | " +
            "Timestamp=%s | ALERT",
            oddsId, matchDate, reason,
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        
        // WARN level - might be legitimate correction, but suspicious
        SECURITY_LOG.warn(message);
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // BUSINESS LOGIC ANOMALIES
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Log anomalous bookmaker margins.
     * 
     * Normal bookmaker margin: 2-10%
     * Suspicious margins:
     * - < 0% (bookmaker would lose money - impossible!)
     * - < 1% (too low - likely error)
     * - > 20% (too high - unfair to customers)
     * 
     * Example log output:
     * 2025-11-03 15:10:00.567 | SECURITY | ANOMALOUS_MARGIN | 
     * OddsID=123 | Match=Barcelona vs Real Madrid | Margin=-2.50% | 
     * Timestamp=2025-11-03 15:10:00.567 | SUSPICIOUS
     * 
     * @param oddsId Unique ID of odds
     * @param margin Calculated margin percentage
     * @param homeTeam Home team name
     * @param awayTeam Away team name
     */
    public void logAnomalousMargin(Long oddsId, double margin, 
                                    String homeTeam, String awayTeam) {
        String message = String.format(
            "ANOMALOUS_MARGIN | OddsID=%d | Match=%s vs %s | Margin=%.2f%% | " +
            "Timestamp=%s | SUSPICIOUS",
            oddsId, homeTeam, awayTeam, margin,
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        
        // WARN level - might be error, might be fraud
        SECURITY_LOG.warn(message);
    }
    
    /**
     * Log repeated validation failures.
     * 
     * Multiple validation failures from same source could indicate:
     * - Buggy client software
     * - User doesn't understand API
     * - Malicious probing (finding weaknesses)
     * 
     * Example log output:
     * 2025-11-03 15:15:30.890 | SECURITY | MALICIOUS_INPUT | 
     * Endpoint=/api/odds | Errors=homeOdds:too low, sport:invalid | 
     * Attempts=15 | Timestamp=2025-11-03 15:15:30.890 | SUSPICIOUS
     * 
     * @param endpoint API endpoint
     * @param fieldErrors Validation errors
     * @param attemptCount Number of failed attempts
     */
    public void logMaliciousValidationFailure(String endpoint, String fieldErrors, 
                                                int attemptCount) {
        String message = String.format(
            "MALICIOUS_INPUT | Endpoint=%s | Errors=%s | Attempts=%d | " +
            "Timestamp=%s | SUSPICIOUS",
            endpoint, fieldErrors, attemptCount,
            LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        
        // ERROR level - repeated failures are suspicious
        SECURITY_LOG.error(message);
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // HELPER METHODS
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Sanitize input for logging.
     * 
     * Prevents:
     * - Log injection attacks (attacker injects fake log entries)
     * - Log file corruption
     * - Excessive log size (truncate very long input)
     * 
     * Sanitization:
     * - Replace newlines (\n) with \\n
     * - Replace carriage returns (\r) with \\r
     * - Replace tabs (\t) with \\t
     * - Limit length to 200 characters
     * 
     * @param input Raw input string
     * @return Sanitized string safe for logging
     */
    private String sanitizeForLog(String input) {
        if (input == null) {
            return "null";
        }
        
        // Replace control characters to prevent log injection
        String sanitized = input
                .replace("\n", "\\n")  // Prevent newline injection
                .replace("\r", "\\r")  // Prevent carriage return
                .replace("\t", "\\t"); // Prevent tab injection
        
        // Limit length to prevent log file bloat
        if (sanitized.length() > 200) {
            sanitized = sanitized.substring(0, 200) + "...[truncated]";
        }
        
        return sanitized;
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // FUTURE ENHANCEMENTS (Phase 3: Security & Authentication)
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Future security logging methods when authentication is added:
     * 
     * 1. LOGIN TRACKING
     *    public void logFailedLogin(String username, String ipAddress, String reason)
     *    public void logSuccessfulLogin(String username, String ipAddress)
     *    public void logSuspiciousLoginPattern(String username, String reason)
     * 
     * 2. SESSION MANAGEMENT
     *    public void logSessionCreated(String sessionId, String userId)
     *    public void logSessionExpired(String sessionId, String reason)
     *    public void logSessionHijackAttempt(String sessionId)
     * 
     * 3. PRIVILEGE CHANGES
     *    public void logPasswordChange(String userId, String username)
     *    public void logRoleChange(String userId, String oldRole, String newRole)
     *    public void logPrivilegeEscalation(String userId, String attemptedAction)
     * 
     * 4. TOKEN SECURITY
     *    public void logJwtTokenExpired(String userId)
     *    public void logJwtTokenRevoked(String tokenId, String reason)
     *    public void logJwtTokenTampering(String suspiciousToken)
     * 
     * 5. IP TRACKING
     *    public void logIpAddressChange(String userId, String oldIp, String newIp)
     *    public void logBlacklistedIpAttempt(String ipAddress)
     */
}