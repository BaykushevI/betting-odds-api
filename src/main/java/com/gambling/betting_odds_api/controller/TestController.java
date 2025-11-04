package com.gambling.betting_odds_api.controller;
// Internal project imports
import com.gambling.betting_odds_api.model.Role;           // User role enum
import com.gambling.betting_odds_api.model.User;           // User entity
import com.gambling.betting_odds_api.repository.UserRepository; // User data access
// Lombok - Logging
import lombok.RequiredArgsConstructor; // Constructor injection
import lombok.extern.slf4j.Slf4j;      // Logger instance
// Spring Security - Password encoding
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Password hashing
import com.gambling.betting_odds_api.security.JwtTokenProvider; // JWT token generation
// Spring Web - REST endpoints
import org.springframework.http.ResponseEntity; // HTTP response wrapper
import org.springframework.web.bind.annotation.*; // REST annotations
// Java Standard Library
import java.util.HashMap; // Key-value pairs
import java.util.Map;     // Map interface
/**
TestController - TEMPORARY controller for testing Phase 3 features.
WARNING: This controller is for DEVELOPMENT/TESTING ONLY!
DELETE this file before production deployment.
Purpose:
Test password encoding (BCrypt)
Test user creation in database
Verify User entity and repository work correctly
Endpoints:
POST /api/test/create-user - Create a test user
GET /api/test/users - List all users
TODO: DELETE THIS FILE IN WEEK 2 (after proper AuthController is created)
*/
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    /**
    POST /api/test/create-user - Create a test user.
    Example request:
    
    POST http://localhost:8080/api/test/create-user?username=john&password=secret123
    @param username Username for new user
    @param password Plain text password (will be hashed)
    @return Created user details
    */
    @PostMapping("/create-user")
    public ResponseEntity<Map<String, Object>> createTestUser(
    @RequestParam String username,
    @RequestParam String password) {
        log.info("Creating test user: {}", username);
        // Hash password with BCrypt
        String hashedPassword = passwordEncoder.encode(password);
        log.debug("Password hashed successfully. Length: {}", hashedPassword.length());
        // Create user entity
        User user = User.builder()
            .username(username)
            .email(username + "@test.com")
            .password(hashedPassword)
            .role(Role.USER)
            .active(true)
            .build();
        // Save to database
        User savedUser = userRepository.save(user);
        log.info("User created with ID: {}", savedUser.getId());
        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User created successfully");
        response.put("id", savedUser.getId());
        response.put("username", savedUser.getUsername());
        response.put("email", savedUser.getEmail());
        response.put("role", savedUser.getRole());
        response.put("hashedPassword", hashedPassword);
        response.put("passwordLength", hashedPassword.length());
        return ResponseEntity.ok(response);
    }

    /**

    GET /api/test/users - List all users (without passwords).

    Example request:
    GET http://localhost:8080/api/test/users

    @return List of all users
    */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        log.info("Fetching all users");
        return ResponseEntity.ok(userRepository.findAll());
    }
    /**
     * POST /api/test/generate-token - Generate JWT token for a user.
     * 
     * Example request:
     * POST http://localhost:8080/api/test/generate-token?username=john
     * 
     * @param username Username to generate token for
     * @return JWT token and details
     */
    @PostMapping("/generate-token")
    public ResponseEntity<Map<String, Object>> generateToken(
            @RequestParam String username) {
        
        log.info("Generating JWT token for user: {}", username);
        
        // Generate token
        String token = jwtTokenProvider.generateToken(username);
        
        // Extract token details
        String extractedUsername = jwtTokenProvider.extractUsername(token);
        java.util.Date expiration = jwtTokenProvider.extractExpiration(token);
        boolean isValid = jwtTokenProvider.validateToken(token, username);
        
        log.info("Token generated successfully for user: {}", username);
        log.debug("Token length: {} characters", token.length());
        log.debug("Token expires at: {}", expiration);
        
        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("message", "JWT token generated successfully");
        response.put("token", token);
        response.put("username", extractedUsername);
        response.put("expiresAt", expiration);
        response.put("isValid", isValid);
        response.put("tokenLength", token.length());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * POST /api/test/validate-token - Validate a JWT token.
     * 
     * Example request:
     * POST http://localhost:8080/api/test/validate-token?token=eyJhbGci...&username=john
     * 
     * @param token JWT token to validate
     * @param username Username to validate against
     * @return Validation result
     */
    @PostMapping("/validate-token")
    public ResponseEntity<Map<String, Object>> validateToken(
            @RequestParam String token,
            @RequestParam String username) {
        
        log.info("Validating JWT token for user: {}", username);
        
        try {
            // Validate token
            boolean isValid = jwtTokenProvider.validateToken(token, username);
            
            // Extract token details
            String extractedUsername = jwtTokenProvider.extractUsername(token);
            java.util.Date expiration = jwtTokenProvider.extractExpiration(token);
            boolean isExpired = expiration.before(new java.util.Date());
            
            log.info("Token validation result: {}", isValid);
            
            // Build response
            Map<String, Object> response = new HashMap<>();
            response.put("isValid", isValid);
            response.put("extractedUsername", extractedUsername);
            response.put("expiresAt", expiration);
            response.put("isExpired", isExpired);
            response.put("usernameMatches", extractedUsername.equals(username));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("isValid", false);
            response.put("error", e.getMessage());
            
            return ResponseEntity.ok(response);
        }
    }
}