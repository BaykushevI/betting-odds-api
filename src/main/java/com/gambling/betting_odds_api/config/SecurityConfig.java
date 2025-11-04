package com.gambling.betting_odds_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
/**

SecurityConfig - Spring Security Configuration

═══════════════════════════════════════════════════════════════════════════
PHASE 3 - TEMPORARY CONFIGURATION (Week 1)
═══════════════════════════════════════════════════════════════════════════

This is a TEMPORARY configuration that disables security completely.
This allows us to:
Add Spring Security dependencies
Keep existing tests passing 
Build proper JWT authentication step-by-step
NEXT STEPS (Week 2):
Add User entity and repository
Add JWT token generation
Add JWT authentication filter
Enable security with JWT validation
═══════════════════════════════════════════════════════════════════════════
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
        // ═══════════════════════════════════════════════════════════════
        // DISABLE CSRF - Not needed for stateless JWT authentication
        // ═══════════════════════════════════════════════════════════════
        // CSRF (Cross-Site Request Forgery) protection is for session-based auth.
        // We're using JWT tokens (stateless), so CSRF doesn't apply.
        .csrf(csrf -> csrf.disable())
        // ═══════════════════════════════════════════════════════════════
        // AUTHORIZATION RULES - TEMPORARY: All endpoints public
        // ═══════════════════════════════════════════════════════════════
        .authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll()  // TODO: Change this in Week 2!
        )
        
        // ═══════════════════════════════════════════════════════════════
        // SESSION MANAGEMENT - Stateless (no server-side sessions)
        // ═══════════════════════════════════════════════════════════════
        // We use JWT tokens instead of sessions.
        // Server doesn't store any session data.
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
        return http.build();
    }
}