package com.gambling.betting_odds_api.config;
// Internal project imports
import com.gambling.betting_odds_api.security.JwtAuthenticationFilter; // JWT authentication filter

import jakarta.servlet.http.HttpServletResponse; // HTTP response for exception handling

// Spring Context - Bean definitions and configuration
import org.springframework.context.annotation.Bean;        // Marks method as bean factory
import org.springframework.context.annotation.Configuration; // Marks class as configuration source
// Spring Security - Web security configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // Enable @PreAuthorize
import org.springframework.security.config.annotation.web.builders.HttpSecurity;           // HTTP security rules
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // Enables Spring Security
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // HTTP configurer
import org.springframework.security.config.http.SessionCreationPolicy;                     // Session management policies
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;                   // Password hashing algorithm
import org.springframework.security.web.SecurityFilterChain;                                // Security filter configuration
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Authentication filter
// Lombok - Reduces boilerplate code
import lombok.RequiredArgsConstructor; // Constructor injection for final fields
/**
SecurityConfig - Spring Security Configuration for JWT Authentication.
PHASE 3 - PRODUCTION CONFIGURATION (Week 2 Day 7)
This configuration:
Enables JWT-based authentication (stateless)
Disables CSRF (not needed for stateless APIs)
Configures public and protected endpoints
Adds JwtAuthenticationFilter to security chain
Configures exception handling for unauthorized requests
Public Endpoints (no authentication required):
POST /api/auth/register - User registration
POST /api/auth/login - User login
Protected Endpoints (authentication required):
All other endpoints (e.g., /api/odds/**)
Authentication Flow:
Client sends request with Authorization: Bearer <token>
JwtAuthenticationFilter intercepts request
Filter validates token and loads user
Filter sets authentication in SecurityContext
Request proceeds to controller (user is authenticated)
Session Management:
STATELESS - No server-side sessions
JWT tokens are used for authentication
SecurityContext is cleared after each request
*/
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Enables @PreAuthorize, @Secured, @RolesAllowed annotations
@RequiredArgsConstructor
public class SecurityConfig {
    // Dependency injected via constructor
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Security Filter Chain - Defines security rules for HTTP requests.
     * 
     * Configuration:
     *   1. CSRF disabled (not needed for stateless JWT authentication)
     *   2. Authorization rules (public vs protected endpoints)
     *   3. Session management (stateless)
     *   4. JWT filter added before UsernamePasswordAuthenticationFilter
     *   5. Exception handling (unauthorized/forbidden responses)
     * 
     * Filter Chain Order:
     *   JwtAuthenticationFilter → UsernamePasswordAuthenticationFilter → ... → Controller
     * 
     * @param http HttpSecurity configuration
     * @return SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // DISABLE CSRF - Not needed for stateless JWT authentication
                // CSRF (Cross-Site Request Forgery) protection is for session-based auth.
                // We're using JWT tokens (stateless), so CSRF doesn't apply.
                .csrf(AbstractHttpConfigurer::disable)

                // AUTHORIZATION RULES - Define which endpoints are public/protected
                .authorizeHttpRequests(auth -> auth
                        // PUBLIC ENDPOINTS - No authentication required
                        // Allow registration and login without token
                        .requestMatchers("/api/auth/**").permitAll()
                        
                        // PROTECTED ENDPOINTS - Authentication required
                        // All other endpoints require valid JWT token
                        // Examples: /api/odds/**, /api/users/**, etc.
                        .anyRequest().authenticated()
                )

                // SESSION MANAGEMENT - Stateless (no server-side sessions)
                // We use JWT tokens instead of sessions.
                // Server doesn't store any session data.
                // SecurityContext is request-scoped (cleared after response).
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // ADD JWT AUTHENTICATION FILTER
                // Add our custom filter BEFORE Spring Security's UsernamePasswordAuthenticationFilter
                // This ensures JWT authentication happens first
                // Filter chain: JwtAuthenticationFilter → UsernamePasswordAuthenticationFilter → ...
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                // EXCEPTION HANDLING - Configure responses for authentication errors
                // authenticationEntryPoint: Handles 401 Unauthorized (no/invalid token)
                // accessDeniedHandler: Handles 403 Forbidden (valid token but insufficient permissions)
                .exceptionHandling(exception -> exception
                        // 401 Unauthorized - No authentication or invalid token
                        // Triggered when: No token, invalid token, expired token
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write(
                                    "{\"error\": \"Unauthorized\", " +
                                    "\"message\": \"Authentication required. Please provide a valid JWT token.\"}"
                            );
                        })
                        
                        // 403 Forbidden - Authenticated but insufficient permissions
                        // Triggered when: Valid token but user doesn't have required role
                        // Example: USER trying to access ADMIN-only endpoint
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write(
                                    "{\"error\": \"Forbidden\", " +
                                    "\"message\": \"You don't have permission to access this resource.\"}"
                            );
                        })
                );

        return http.build();
    }

    /**
     * Password Encoder - BCrypt hashing algorithm.
     * 
     * BCrypt is the recommended password encoder for Spring Security.
     * 
     * How BCrypt works:
     *   - Takes plain text password
     *   - Adds random salt (prevents rainbow table attacks)
     *   - Hashes with multiple rounds (default: 10 rounds)
     *   - Produces 60-character hash
     * 
     * Example:
     *   Plain:   "password123"
     *   BCrypt:  "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
     * 
     * Security features:
     *   - Different hash each time (due to random salt)
     *   - Slow by design (prevents brute force)
     *   - One-way function (cannot decrypt)
     * 
     * @return BCryptPasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}