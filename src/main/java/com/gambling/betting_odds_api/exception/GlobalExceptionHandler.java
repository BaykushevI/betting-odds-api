package com.gambling.betting_odds_api.exception;

// INTERNAL PROJECT IMPORTS
import com.gambling.betting_odds_api.logging.AuditLogger;
import com.gambling.betting_odds_api.logging.SecurityLogger;

// LOMBOK
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// SPRING WEB
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// SPRING VALIDATION
import org.springframework.validation.FieldError;

// SPRING WEB - Exception handling
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

// JAVA STANDARD LIBRARY
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {
    
    private final AuditLogger auditLogger;
    private final SecurityLogger securityLogger;
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        
        Map<String, String> fieldErrors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        
        String endpoint = extractEndpoint(request);
        
        String errorSummary = fieldErrors.entrySet().stream()
                .map(e -> e.getKey() + ":" + e.getValue())
                .collect(Collectors.joining(", "));
        
        log.warn("Validation failed for {}: {}", endpoint, errorSummary);
        
        auditLogger.logValidationFailure(endpoint, errorSummary);
        
        // Check BOTH error messages AND actual input values for suspicious patterns
        boolean hasSuspiciousInput = containsSuspiciousInput(fieldErrors) || 
                                      containsSuspiciousRejectedValues(ex);
        
        if (hasSuspiciousInput) {
            securityLogger.logMaliciousValidationFailure(endpoint, errorSummary, 1);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        response.put("message", "Invalid input data");
        response.put("fieldErrors", fieldErrors);
        response.put("path", endpoint);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, 
            WebRequest request) {
        
        String endpoint = extractEndpoint(request);
        
        log.warn("Resource not found: {} at {}", ex.getMessage(), endpoint);
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                endpoint
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(InvalidOddsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInvalidOddsException(
            InvalidOddsException ex, 
            WebRequest request) {
        
        String endpoint = extractEndpoint(request);
        
        log.warn("Invalid odds: {} at {}", ex.getMessage(), endpoint);
        
        auditLogger.logValidationFailure(endpoint, "InvalidOdds: " + ex.getMessage());
        
        securityLogger.logInvalidOdds(
            "Unknown", 
            "Unknown", 
            0.0, 0.0, 0.0,
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
    
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, 
            WebRequest request) {
        
        String endpoint = extractEndpoint(request);
        
        log.warn("Illegal argument: {} at {}", ex.getMessage(), endpoint);
        
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
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, 
            WebRequest request) {
        
        String endpoint = extractEndpoint(request);
        
        log.error("Unexpected error at {}: {}", endpoint, ex.getMessage(), ex);
        
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
    
    // HELPER METHODS
    
    private String extractEndpoint(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
    
    private boolean containsSuspiciousInput(Map<String, String> fieldErrors) {
        String allErrors = fieldErrors.toString().toLowerCase();
        
        if (allErrors.contains("'") || 
            allErrors.contains("--") || 
            allErrors.contains("union") || 
            allErrors.contains("select") ||
            allErrors.contains("drop") ||
            allErrors.contains("insert")) {
            return true;
        }
        
        if (allErrors.contains("<script") || 
            allErrors.contains("<iframe") ||
            allErrors.contains("javascript:") ||
            allErrors.contains("onerror=")) {
            return true;
        }
        
        if (allErrors.contains("../") || allErrors.contains("..\\")) {
            return true;
        }
        
        return false;
    }
    
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
    
    /**
     * Check if rejected values contain suspicious patterns.
     * This checks the ACTUAL INPUT VALUES, not just error messages.
     */
    private boolean containsSuspiciousRejectedValues(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
                .anyMatch(error -> {
                    Object rejectedValue = error.getRejectedValue();
                    if (rejectedValue == null) return false;
                    
                    String value = rejectedValue.toString();
                    
                    // Check for SQL injection patterns
                    if (value.contains("'") || 
                        value.contains("--") || 
                        value.toLowerCase().contains("union") ||
                        value.toLowerCase().contains("select") ||
                        value.toLowerCase().contains("drop")) {
                        
                        log.warn("Suspicious input detected in field {}: {}", 
                                error.getField(), value);
                        return true;
                    }
                    
                    // Check for XSS patterns
                    if (value.contains("<script") || 
                        value.contains("<iframe") ||
                        value.contains("javascript:")) {
                        
                        log.warn("XSS attempt detected in field {}: {}", 
                                error.getField(), value);
                        return true;
                    }
                    
                    return false;
                });
    }
}