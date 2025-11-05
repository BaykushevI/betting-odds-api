package com.gambling.betting_odds_api.controller;
// Internal project imports
import com.gambling.betting_odds_api.dto.AuthResponse;      // Authentication response DTO
import com.gambling.betting_odds_api.dto.LoginRequest;      // Login request DTO
import com.gambling.betting_odds_api.dto.RegisterRequest;   // Registration request DTO
import com.gambling.betting_odds_api.service.AuthService;   // Authentication business logic
// Spring Framework - Web MVC and validation
import org.springframework.http.HttpStatus;                  // HTTP status codes
import org.springframework.http.ResponseEntity;              // HTTP response wrapper
import org.springframework.web.bind.annotation.PostMapping;  // POST endpoint annotation
import org.springframework.web.bind.annotation.RequestBody;  // Binds HTTP body to method parameter
import org.springframework.web.bind.annotation.RequestMapping; // Base path for controller
import org.springframework.web.bind.annotation.RestController; // REST API controller
// Jakarta Validation - Bean validation
import jakarta.validation.Valid; // Triggers validation on request body
// Lombok - Reduces boilerplate code
import lombok.RequiredArgsConstructor; // Constructor injection for final fields
import lombok.extern.slf4j.Slf4j;      // Logger instance
/**
AuthController - REST API endpoints for authentication.
Base URL: /api/auth
Endpoints:
POST /api/auth/register - Register new user
POST /api/auth/login    - Login existing user
Request/Response Flow:
Client sends POST request with JSON body
@Valid triggers validation (checks @NotBlank, @Email, @Size)
Controller delegates to AuthService
AuthService handles business logic (DB, password hashing, JWT)
Controller wraps response in ResponseEntity with HTTP status
Client receives JSON response with JWT token
Validation:
Automatic validation via @Valid annotation
Returns 400 Bad Request if validation fails
Validation errors from RegisterRequest and LoginRequest constraints
Exception Handling:
IllegalArgumentException → 400 Bad Request (duplicate user/email)
RuntimeException → 401 Unauthorized (invalid credentials)
Other exceptions → 500 Internal Server Error
Security Notes:
Endpoints are public (no authentication required)
Password is never returned in response
JWT token is returned for authenticated requests
Token should be sent in Authorization header: "Bearer <token>"
*/
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    // Dependency injected via constructor (@RequiredArgsConstructor)
    private final AuthService authService;

    /**
     * Register a new user.
     * 
     * Endpoint: POST /api/auth/register
     * 
     * Request body (JSON):
     * {
     *   "username": "john",
     *   "email": "john@example.com",
     *   "password": "password123"
     * }
     * 
     * Success response (200 OK):
     * {
     *   "token": "eyJhbGciOiJIUzI1NiJ9...",
     *   "tokenType": "Bearer",
     *   "username": "john",
     *   "email": "john@example.com",
     *   "role": "USER"
     * }
     * 
     * Error responses:
     *   400 Bad Request - Validation failed or duplicate username/email
     *   500 Internal Server Error - Unexpected error
     * 
     * Validation:
     *   - username: 3-50 characters, not blank
     *   - email: valid email format, not blank
     *   - password: 8+ characters, not blank
     * 
     * @param request RegisterRequest containing username, email, password
     * @return ResponseEntity with AuthResponse and HTTP status
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        log.info("POST /api/auth/register - Register attempt for username: {}", request.getUsername());

        try {
            // Delegate to AuthService for business logic
            // AuthService handles: validation, password hashing, DB save, JWT generation
            AuthResponse response = authService.register(request);
            
            log.info("User registered successfully: {}", request.getUsername());
            
            // Return 200 OK with AuthResponse
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            // Handle duplicate username or email
            // IllegalArgumentException is thrown by AuthService when user/email exists
            log.warn("Registration failed for {}: {}", request.getUsername(), e.getMessage());
            
            // Return 400 Bad Request with error message
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
                    
        } catch (Exception e) {
            // Handle unexpected errors
            log.error("Unexpected error during registration for {}: {}", request.getUsername(), e.getMessage(), e);
            
            // Return 500 Internal Server Error
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Registration failed. Please try again later."));
        }
    }

    /**
     * Authenticate user and generate JWT token.
     * 
     * Endpoint: POST /api/auth/login
     * 
     * Request body (JSON):
     * {
     *   "username": "john",
     *   "password": "password123"
     * }
     * 
     * Success response (200 OK):
     * {
     *   "token": "eyJhbGciOiJIUzI1NiJ9...",
     *   "tokenType": "Bearer",
     *   "username": "john",
     *   "email": "john@example.com",
     *   "role": "USER"
     * }
     * 
     * Error responses:
     *   400 Bad Request - Validation failed
     *   401 Unauthorized - Invalid credentials or inactive account
     *   500 Internal Server Error - Unexpected error
     * 
     * Validation:
     *   - username: not blank
     *   - password: not blank
     * 
     * Security:
     *   - Password is verified with BCrypt (secure comparison)
     *   - Inactive accounts cannot login
     *   - JWT token is generated only after successful authentication
     * 
     * @param request LoginRequest containing username and password
     * @return ResponseEntity with AuthResponse and HTTP status
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/auth/login - Login attempt for username: {}", request.getUsername());

        try {
            // Delegate to AuthService for business logic
            // AuthService handles: user lookup, password verification, JWT generation
            AuthResponse response = authService.login(request);
            
            log.info("User logged in successfully: {}", request.getUsername());
            
            // Return 200 OK with AuthResponse
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            // Handle invalid credentials or inactive account
            // RuntimeException is thrown by AuthService for authentication failures
            log.warn("Login failed for {}: {}", request.getUsername(), e.getMessage());
            
            // Return 401 Unauthorized with error message
            // Security note: Don't reveal whether username or password is wrong
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage()));
                    
        } catch (Exception e) {
            // Handle unexpected errors
            log.error("Unexpected error during login for {}: {}", request.getUsername(), e.getMessage(), e);
            
            // Return 500 Internal Server Error
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Login failed. Please try again later."));
        }
    }

    /**
     * ErrorResponse - Simple DTO for error messages.
     * 
     * Used for consistent error response format across all endpoints.
     * 
     * Example JSON:
     * {
     *   "message": "Username already exists"
     * }
     * 
     * Note: In Phase 4, we'll create a proper global exception handler
     *       with more detailed error responses (timestamp, path, status, etc.)
     */
    private record ErrorResponse(String message) {
        // Java 16+ record - immutable data class
        // Automatically generates: constructor, getters, equals, hashCode, toString
    }
}