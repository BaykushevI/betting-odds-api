package com.gambling.betting_odds_api.exception;

// ═══════════════════════════════════════════════════════════════════════════
// INTERNAL PROJECT IMPORTS
// ═══════════════════════════════════════════════════════════════════════════
// Logging - Professional logging system
import com.gambling.betting_odds_api.logging.AuditLogger;
import com.gambling.betting_odds_api.logging.SecurityLogger;

// ═══════════════════════════════════════════════════════════════════════════
// LOMBOK - Reduces boilerplate code
// ═══════════════════════════════════════════════════════════════════════════
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// ═══════════════════════════════════════════════════════════════════════════
// SPRING WEB - HTTP response handling
// ═══════════════════════════════════════════════════════════════════════════
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// ═══════════════════════════════════════════════════════════════════════════
// SPRING VALIDATION - Bean validation support
// ═══════════════════════════════════════════════════════════════════════════
import org.springframework.validation.FieldError;

// ═══════════════════════════════════════════════════════════════════════════
// SPRING WEB - Exception handling
// ═══════════════════════════════════════════════════════════════════════════
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

// ═══════════════════════════════════════════════════════════════════════════
// JAVA STANDARD LIBRARY - Core Java classes
// ═══════════════════════════════════════════════════════════════════════════
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * GlobalExceptionHandler - Centralized exception handling with comprehensive logging.
 * 
 * Logging Strategy:
 * - Standard logging (log.*) for all exceptions
 * - AuditLogger for tracking validation failures
 * - SecurityLogger for detecting suspicious patterns
 */
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {
    
    // ═══════════════════════════════════════════════════════════════════════
    // DEPENDENCIES - Injected via constructor
    // ═══════════════════════════════════════════════════════════════════════
    
    private final AuditLogger auditLogger;
    private final SecurityLogger securityLogger;
    
    // ═══════════════════════════════════════════════════════════════════════
    // VALIDATION ERRORS - 400 Bad Request
    // ═══════════════════════════════════════════════════════════════════════
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        
        // Extract field-specific validation errors
        Map<String, String> fieldErrors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        
        // Extract endpoint path
        String endpoint = extractEndpoint(request);
        
        // Build error summary for logging
        String errorSummary = fieldErrors.entrySet().stream()
                .map(e -> e.getKey() + ":" + e.getValue())
                .collect(Collectors.joining(", "));
        
        // Standard logging
        log.warn("Validation failed for {}: {}", endpoint, errorSummary);
        
        // Audit log - track validation failures for compliance
        auditLogger.logValidationFailure(endpoint, errorSummary);
        
        // Security log - detect suspicious patterns
        if (containsSuspiciousInput(fieldErrors)) {
            securityLogger.logMaliciousValidationFailure(
                endpoint, 
                errorSummary, 
                1 // In future, track attempt count per IP/user
            );
        }
        
        // Build error response
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        response.put("message", "Invalid input data");
        response.put("fieldErrors", fieldErrors);
        response.put("path", endpoint);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // RESOURCE NOT FOUND - 404 Not Found
    // ═══════════════════════════════════════════════════════════════════════
    
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, 
            WebRequest request) {
        
        String endpoint = extractEndpoint(request);
        
        // Standard logging
        log.warn("Resource not found: {} at {}", ex.getMessage(), endpoint);
        
        // Note: No audit/security logging for 404s - they're common and not suspicious
        // Unless we detect a pattern (e.g., scanning for valid IDs)
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                endpoint
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // INVALID ODDS - 400 Bad Request
    // ═══════════════════════════════════════════════════════════════════════
    
    @ExceptionHandler(InvalidOddsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInvalidOddsException(
            InvalidOddsException ex, 
            WebRequest request) {
        
        String endpoint = extractEndpoint(request);
        
        // Standard logging
        log.warn("Invalid odds: {} at {}", ex.getMessage(), endpoint);
        
        // Audit log - business rule violation
        auditLogger.logValidationFailure(endpoint, "InvalidOdds: " + ex.getMessage());
        
        // Security log - might indicate fraud attempt
        securityLogger.logInvalidOdds(
            "Unknown", // homeTeam - not available in exception
            "Unknown", // awayTeam - not available in exception
            0.0, 0.0, 0.0, // odds values - not available
            ex.getMessage()
        );
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                endpoint
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // ILLEGAL ARGUMENT - 400 Bad Request
    // ═══════════════════════════════════════════════════════════════════════
    
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, 
            WebRequest request) {
        
        String endpoint = extractEndpoint(request);
        
        // Standard logging
        log.warn("Illegal argument: {} at {}", ex.getMessage(), endpoint);
        
        // Check if this looks like an injection attempt
        String message = ex.getMessage();
        if (message != null && containsSqlInjectionPattern(message)) {
            securityLogger.logSqlInjectionAttempt(endpoint, message);
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                endpoint
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // CATCH-ALL - 500 Internal Server Error
    // ═══════════════════════════════════════════════════════════════════════
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, 
            WebRequest request) {
        
        String endpoint = extractEndpoint(request);
        
        // Standard logging with full stack trace
        log.error("Unexpected error at {}: {}", endpoint, ex.getMessage(), ex);
        
        // Audit log - unexpected errors should be tracked
        auditLogger.logCustomEvent(
            "UNEXPECTED_ERROR",
            String.format("Endpoint=%s, Error=%s", endpoint, ex.getMessage())
        );
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred: " + ex.getMessage(),
                endpoint
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // HELPER METHODS
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Extract clean endpoint path from request.
     */
    private String extractEndpoint(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
    
    /**
     * Check if validation errors contain suspicious patterns.
     * 
     * Suspicious indicators:
     * - SQL keywords in errors (', --, UNION, SELECT)
     * - Script tags in errors (<script>, <iframe>)
     * - Path traversal in errors (../, ..\)
     * - Null bytes (\0)
     */
    private boolean containsSuspiciousInput(Map<String, String> fieldErrors) {
        String allErrors = fieldErrors.toString().toLowerCase();
        
        // SQL injection patterns
        if (allErrors.contains("'") || 
            allErrors.contains("--") || 
            allErrors.contains("union") || 
            allErrors.contains("select") ||
            allErrors.contains("drop") ||
            allErrors.contains("insert")) {
            return true;
        }
        
        // XSS patterns
        if (allErrors.contains("<script") || 
            allErrors.contains("<iframe") ||
            allErrors.contains("javascript:") ||
            allErrors.contains("onerror=")) {
            return true;
        }
        
        // Path traversal
        if (allErrors.contains("../") || allErrors.contains("..\\")) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Check if string contains SQL injection pattern.
     */
    private boolean containsSqlInjectionPattern(String input) {
        if (input == null) return false;
        
        String lower = input.toLowerCase();
        
        return lower.contains("'") ||
               lower.contains("--") ||
               lower.contains("union") ||
               lower.contains("select") ||
               lower.contains("drop") ||
               lower.contains("insert") ||
               lower.contains("delete") ||
               lower.contains(";");
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // FUTURE ENHANCEMENTS (Phase 3: Security & Authentication)
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Future exception handlers to add:
     * 
     * @ExceptionHandler(AccessDeniedException.class)
     * - Log unauthorized access attempts
     * - Track patterns (same user trying multiple restricted endpoints)
     * - Alert on privilege escalation attempts
     * 
     * @ExceptionHandler(AuthenticationException.class)
     * - Log failed authentication
     * - Track brute force attempts (same IP, multiple failures)
     * - Alert on credential stuffing patterns
     * 
     * @ExceptionHandler(RateLimitExceededException.class)
     * - Log rate limit violations
     * - Track source IP and patterns
     * - Detect distributed attacks (multiple IPs)
     * 
     * @ExceptionHandler(DataIntegrityViolationException.class)
     * - Log database constraint violations
     * - Might indicate bug or malicious data manipulation
     * 
     * IP Tracking Enhancement:
     * - Extract client IP from request
     * - Track failed attempts per IP
     * - Auto-block after N failures
     * - Log IP geolocation for suspicious activity
     * 
     * Example:
     * private String extractClientIp(HttpServletRequest request) {
     *     String xForwardedFor = request.getHeader("X-Forwarded-For");
     *     return (xForwardedFor != null) ? xForwardedFor.split(",")[0] : request.getRemoteAddr();
     * }
     */
}