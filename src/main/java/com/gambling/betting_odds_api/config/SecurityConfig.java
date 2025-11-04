package com.gambling.betting_odds_api.config;
// Spring Context - Bean definitions and configuration
import org.springframework.context.annotation.Bean;        // Marks method as bean factory
import org.springframework.context.annotation.Configuration; // Marks class as configuration source
// Spring Security - Web security configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity;           // HTTP security rules
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // Enables Spring Security
import org.springframework.security.config.http.SessionCreationPolicy;                     // Session management policies
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;                   // Password hashing algorithm
import org.springframework.security.web.SecurityFilterChain;                                // Security filter configuration
/**
SecurityConfig - Spring Security Configuration
PHASE 3 - TEMPORARY CONFIGURATION (Week 1)
This is a TEMPORARY configuration that disables security completely.
This allows us to:
Add Spring Security dependencies
Keep existing tests passing
Build proper JWT authentication step-by-step
NEXT STEPS (Week 2):
Add User entity and repository (DONE)
Add JWT token generation
Add JWT authentication filter
Enable security with JWT validation
*/
@Configuration
@EnableWebSecurity
public class SecurityConfig {

/**
Security Filter Chain - Defines security rules for HTTP requests.
CURRENT STATE: All endpoints are PUBLIC (permitAll).
This is temporary for Phase 3 Week 1.
@param http HttpSecurity configuration
@return SecurityFilterChain
@throws Exception if configuration fails
*/
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        // DISABLE CSRF - Not needed for stateless JWT authentication
        // CSRF (Cross-Site Request Forgery) protection is for session-based auth.
        // We're using JWT tokens (stateless), so CSRF doesn't apply.
        .csrf(csrf -> csrf.disable())
        // AUTHORIZATION RULES - TEMPORARY: All endpoints public
        .authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll()  // TODO: Change this in Week 2
        )

        // SESSION MANAGEMENT - Stateless (no server-side sessions)
        // We use JWT tokens instead of sessions.
        // Server doesn't store any session data.
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
        return http.build();
    }
    /**
    Password Encoder - BCrypt hashing algorithm.
    BCrypt is the recommended password encoder for Spring Security.
    How BCrypt works:
    Takes plain text password
    Adds random salt (prevents rainbow table attacks)
    Hashes with multiple rounds (default: 10 rounds)
    Produces 60-character hash
    Example:
    Plain:   "password123"
    BCrypt:  "2a$10
    N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
    Security features:
    Different hash each time (due to random salt)
    Slow by design (prevents brute force)
    One-way function (cannot decrypt)
    @return BCryptPasswordEncoder
    */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}