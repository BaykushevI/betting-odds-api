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
}