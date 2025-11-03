package com.gambling.betting_odds_api.exception;

// ═══════════════════════════════════════════════════════════════════════════
// INTERNAL PROJECT IMPORTS - Custom exceptions
// ═══════════════════════════════════════════════════════════════════════════
// No imports needed - ErrorResponse, ResourceNotFoundException, InvalidOddsException 
// are in the same package

// ═══════════════════════════════════════════════════════════════════════════
// LOMBOK - Reduces boilerplate code
// ═══════════════════════════════════════════════════════════════════════════
import lombok.extern.slf4j.Slf4j; // Auto-generates Logger instance (log variable)

// ═══════════════════════════════════════════════════════════════════════════
// SPRING WEB - HTTP response handling
// ═══════════════════════════════════════════════════════════════════════════
import org.springframework.http.HttpStatus;      // HTTP status codes (400, 404, 500, etc.)
import org.springframework.http.ResponseEntity;  // Wrapper for HTTP response

// ═══════════════════════════════════════════════════════════════════════════
// SPRING VALIDATION - Bean validation support
// ═══════════════════════════════════════════════════════════════════════════
import org.springframework.validation.FieldError; // Represents a field validation error

// ═══════════════════════════════════════════════════════════════════════════
// SPRING WEB - Exception handling
// ═══════════════════════════════════════════════════════════════════════════
import org.springframework.web.bind.MethodArgumentNotValidException; // Validation failed exception
import org.springframework.web.bind.annotation.ExceptionHandler;     // Marks method as exception handler
import org.springframework.web.bind.annotation.ResponseStatus;       // Sets HTTP status for exception
import org.springframework.web.bind.annotation.RestControllerAdvice; // Global exception handler
import org.springframework.web.context.request.WebRequest;           // Provides request details

// ═══════════════════════════════════════════════════════════════════════════
// JAVA STANDARD LIBRARY - Core Java classes
// ═══════════════════════════════════════════════════════════════════════════
import java.time.LocalDateTime; // Timestamp for error responses
import java.util.HashMap;       // Key-value pairs for error details
import java.util.Map;           // Interface for key-value collections

/**
 * GlobalExceptionHandler - Centralized exception handling for all controllers.
 * 
 * ═══════════════════════════════════════════════════════════════════════════
 * WHY GLOBAL EXCEPTION HANDLER?
 * ═══════════════════════════════════════════════════════════════════════════
 * 
 * 1. CENTRALIZED ERROR HANDLING
 *    - Single place to handle all exceptions
 *    - Consistent error response format across entire API
 *    - DRY principle - no try-catch in every controller method
 * 
 * 2. CLEAN CONTROLLERS
 *    - Controllers focus on happy path
 *    - No exception handling clutter
 *    - More readable and maintainable
 * 
 * 3. CONSISTENT ERROR RESPONSES
 *    - Same JSON structure for all errors
 *    - Client knows what to expect
 *    - Easy to parse on frontend
 * 
 * 4. PROPER HTTP STATUS CODES
 *    - 400 Bad Request (validation errors)
 *    - 404 Not Found (resource not found)
 *    - 500 Internal Server Error (unexpected errors)
 * 
 * ═══════════════════════════════════════════════════════════════════════════
 * HOW IT WORKS:
 * ═══════════════════════════════════════════════════════════════════════════
 * 
 * 1. Exception thrown anywhere in controller/service
 * 2. Spring catches exception
 * 3. Spring looks for @ExceptionHandler method matching exception type
 * 4. Handler method executes, returns ResponseEntity
 * 5. Spring converts to HTTP response with proper status code
 * 
 * ═══════════════════════════════════════════════════════════════════════════
 */
@RestControllerAdvice // Global exception handler for all @RestController classes
@Slf4j                // Lombok: generates Logger instance
public class GlobalExceptionHandler {
    
    // Future: Logger classes will be added here in next commits
    // private final AuditLogger auditLogger;
    // private final SecurityLogger securityLogger;
    
    // ═══════════════════════════════════════════════════════════════════════
    // VALIDATION ERRORS - 400 Bad Request
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Handle validation errors from @Valid annotation.
     * 
     * Triggered when:
     * - @Valid fails in controller method parameter
     * - Bean validation rules violated (e.g., @NotNull, @Min, @Max)
     * 
     * Example scenarios:
     * - homeOdds < 1.01 (violates @Min validation)
     * - sport is empty (violates @NotBlank)
     * - matchDate is in the past (violates @Future)
     * 
     * Response format:
     * {
     *   "timestamp": "2025-11-03T14:30:00",
     *   "status": 400,
     *   "error": "Validation Failed",
     *   "message": "Invalid input data",
     *   "fieldErrors": {
     *     "homeOdds": "Home odds must be at least 1.01",
     *     "sport": "Sport must be between 2 and 50 characters"
     *   },
     *   "path": "/api/odds"
     * }
     * 
     * @param ex Validation exception containing field errors
     * @param request Web request context (for path extraction)
     * @return ResponseEntity with 400 status and error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        
        // Extract field-specific validation errors
        Map<String, String> fieldErrors = new HashMap<>();
        
        // Loop through all validation errors
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        
        // Build error response
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        response.put("message", "Invalid input data");
        response.put("fieldErrors", fieldErrors);
        response.put("path", request.getDescription(false).replace("uri=", ""));
        
        // Log validation failure (basic logging for now)
        log.warn("Validation failed for {}: {}", 
                request.getDescription(false).replace("uri=", ""), 
                fieldErrors);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // RESOURCE NOT FOUND - 404 Not Found
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Handle resource not found errors.
     * 
     * Triggered when:
     * - GET /api/odds/{id} with non-existent ID
     * - PUT /api/odds/{id} with non-existent ID
     * - DELETE /api/odds/{id} with non-existent ID
     * 
     * Example response:
     * {
     *   "timestamp": "2025-11-03T14:35:00",
     *   "status": 404,
     *   "error": "Not Found",
     *   "message": "Betting Odds not found with id: 999",
     *   "path": "/api/odds/999"
     * }
     * 
     * @param ex ResourceNotFoundException with custom message
     * @param request Web request context
     * @return ResponseEntity with 404 status and error details
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, 
            WebRequest request) {
        
        // Build structured error response
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        
        // Log not found (WARN level - not an error, but noteworthy)
        log.warn("Resource not found: {} at {}", 
                ex.getMessage(), 
                request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // INVALID ODDS - 400 Bad Request
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Handle invalid odds business rule violations.
     * 
     * Triggered when:
     * - Custom business validation fails in service layer
     * - Example: Margin too high/low (suspicious activity)
     * - Example: Odds values don't make mathematical sense
     * 
     * This is different from bean validation (which checks basic constraints).
     * This handles complex business rules.
     * 
     * @param ex InvalidOddsException with custom message
     * @param request Web request context
     * @return ResponseEntity with 400 status and error details
     */
    @ExceptionHandler(InvalidOddsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInvalidOddsException(
            InvalidOddsException ex, 
            WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        
        // Log invalid odds (WARN level - potential fraud/mistake)
        log.warn("Invalid odds: {} at {}", 
                ex.getMessage(), 
                request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // ILLEGAL ARGUMENT - 400 Bad Request
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Handle illegal argument exceptions.
     * 
     * Triggered when:
     * - Invalid sort parameters (e.g., sort=invalidField,xyz)
     * - Invalid enum values
     * - Null values where not allowed
     * 
     * Example from Controller:
     * - sort=sport,INVALID (not 'asc' or 'desc')
     * 
     * @param ex IllegalArgumentException with message
     * @param request Web request context
     * @return ResponseEntity with 400 status and error details
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, 
            WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        
        // Log illegal argument
        log.warn("Illegal argument: {} at {}", 
                ex.getMessage(), 
                request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // CATCH-ALL - 500 Internal Server Error
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Handle all other unexpected exceptions.
     * 
     * This is the FALLBACK handler for any exception not caught above.
     * 
     * Triggered when:
     * - NullPointerException (bug in code)
     * - Database connection failure
     * - OutOfMemoryError
     * - Any runtime exception not specifically handled
     * 
     * ⚠️ IMPORTANT: This indicates a BUG or SYSTEM FAILURE
     * Should be investigated immediately in production!
     * 
     * Response format:
     * {
     *   "timestamp": "2025-11-03T14:40:00",
     *   "status": 500,
     *   "error": "Internal Server Error",
     *   "message": "An unexpected error occurred: ...",
     *   "path": "/api/odds/123"
     * }
     * 
     * @param ex Any exception not caught by specific handlers
     * @param request Web request context
     * @return ResponseEntity with 500 status and error details
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, 
            WebRequest request) {
        
        // Log full exception with stack trace (ERROR level - this is serious!)
        log.error("Unexpected error at {}: {}", 
                request.getDescription(false).replace("uri=", ""), 
                ex.getMessage(), 
                ex); // Third parameter logs full stack trace
        
        // Build error response (hide internal details from client)
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred: " + ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // FUTURE ENHANCEMENTS (Phase 3: Security & Authentication)
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Future exception handlers to add:
     * 
     * 1. @ExceptionHandler(AccessDeniedException.class)
     *    - Handle 403 Forbidden (user lacks permission)
     *    - Example: USER trying to DELETE odds (only ADMIN allowed)
     * 
     * 2. @ExceptionHandler(AuthenticationException.class)
     *    - Handle 401 Unauthorized (invalid/expired JWT token)
     * 
     * 3. @ExceptionHandler(RateLimitExceededException.class)
     *    - Handle 429 Too Many Requests (rate limit exceeded)
     * 
     * 4. @ExceptionHandler(DataIntegrityViolationException.class)
     *    - Handle database constraint violations
     *    - Example: Duplicate entry, foreign key violation
     * 
     * 5. Enhanced logging with AuditLogger and SecurityLogger
     *    - Track all errors in audit log
     *    - Flag suspicious patterns in security log
     */
}