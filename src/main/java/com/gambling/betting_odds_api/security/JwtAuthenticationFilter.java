package com.gambling.betting_odds_api.security;
// Spring Framework - Web and security
import jakarta.servlet.FilterChain;                    // Filter chain for request processing
import jakarta.servlet.ServletException;               // Servlet exceptions
import jakarta.servlet.http.HttpServletRequest;        // HTTP request
import jakarta.servlet.http.HttpServletResponse;       // HTTP response
import org.springframework.lang.NonNull;               // Non-null annotation
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Authentication token
import org.springframework.security.core.context.SecurityContextHolder; // Security context holder
import org.springframework.security.core.userdetails.UserDetails;        // User details interface
import org.springframework.security.core.userdetails.UserDetailsService; // User details service
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource; // Authentication details
import org.springframework.stereotype.Component;        // Spring component
import org.springframework.web.filter.OncePerRequestFilter; // Filter executed once per request
// Lombok - Reduces boilerplate code
import lombok.RequiredArgsConstructor; // Constructor injection for final fields
import lombok.extern.slf4j.Slf4j;      // Logger instance
// Java Standard Library
import java.io.IOException; // IO exceptions
/**
JwtAuthenticationFilter - Intercepts HTTP requests and validates JWT tokens.
Purpose:
Authenticate users based on JWT tokens in Authorization header.
This filter runs BEFORE Spring Security's authentication mechanisms.
Request Flow:
Client sends request with Authorization header: "Bearer <token>"
Filter extracts token from header
Filter validates token (signature, expiration)
Filter loads user from database (via UserDetailsService)
Filter creates Authentication object and sets it in SecurityContext
Request proceeds to controller (user is authenticated)
Filter Chain Position:
This filter is added BEFORE UsernamePasswordAuthenticationFilter.
It runs on EVERY request (extends OncePerRequestFilter).
Security Notes:
Public endpoints (e.g., /api/auth/**) don't require tokens
Invalid/expired tokens result in 401 Unauthorized
Valid tokens grant access to protected endpoints
SecurityContext is cleared after each request (stateless)
Dependencies:
JwtTokenProvider: Token validation and username extraction
UserDetailsService: Load user data from database
*/
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Dependencies injected via constructor
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    /**
     * Filter logic - Executes on every HTTP request.
     * 
     * Process:
     *   1. Extract JWT token from Authorization header
     *   2. If no token found, skip authentication (continue to next filter)
     *   3. Extract username from token
     *   4. If username is valid and no authentication exists in SecurityContext:
     *      a. Load user details from database
     *      b. Validate token against user
     *      c. Create Authentication object
     *      d. Set Authentication in SecurityContext
     *   5. Continue filter chain
     * 
     * Authorization Header Format:
     *   Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
     * 
     * Why check SecurityContext authentication?
     *   - Avoid redundant database queries if user is already authenticated
     *   - SecurityContext is request-scoped (cleared after response)
     * 
     * @param request HTTP request
     * @param response HTTP response
     * @param filterChain Filter chain to continue processing
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        log.debug("Processing request: {} {}", request.getMethod(), request.getRequestURI());

        try {
            // STEP 1: Extract JWT token from Authorization header
            // Expected format: "Bearer <token>"
            String jwt = extractJwtFromRequest(request);

            // STEP 2: If no token, skip authentication
            // This is normal for public endpoints like /api/auth/login
            if (jwt == null) {
                log.debug("No JWT token found in request");
                filterChain.doFilter(request, response);
                return;
            }

            log.debug("JWT token found, validating...");

            // STEP 3: Extract username from token
            // Token payload contains "sub" (subject) claim with username
            String username = jwtTokenProvider.extractUsername(jwt);

            log.debug("Username extracted from token: {}", username);

            // STEP 4: Authenticate if username is valid and not already authenticated
            // SecurityContextHolder.getContext().getAuthentication() returns current auth
            // If null, user is not authenticated yet (or SecurityContext was cleared)
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                log.debug("User not authenticated, loading user details...");

                // STEP 5: Load user details from database
                // This calls CustomUserDetailsService.loadUserByUsername()
                // Throws UsernameNotFoundException if user doesn't exist
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                log.debug("User details loaded: {}", userDetails.getUsername());

                // STEP 6: Validate token
                // Checks: signature, expiration, username match
                if (jwtTokenProvider.validateToken(jwt, username)) {
                    
                    log.debug("Token is valid, setting authentication in SecurityContext");

                    // STEP 7: Create Authentication object
                    // UsernamePasswordAuthenticationToken is Spring Security's authentication impl
                    // Parameters:
                    //   - principal: UserDetails (authenticated user)
                    //   - credentials: null (we don't need password after authentication)
                    //   - authorities: User roles (from UserDetails)
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,           // Principal (authenticated user)
                                    null,                  // Credentials (not needed)
                                    userDetails.getAuthorities() // Authorities (roles)
                            );

                    // STEP 8: Set authentication details
                    // Includes IP address, session ID, etc. (useful for auditing)
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // STEP 9: Set Authentication in SecurityContext
                    // This makes the user authenticated for this request
                    // Spring Security will use this for authorization decisions
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.info("User authenticated successfully: {}", username);
                    
                } else {
                    log.warn("Token validation failed for user: {}", username);
                }
            }

        } catch (Exception e) {
            // Log error but don't block the request
            // Spring Security will handle unauthorized access (401 response)
            log.error("Cannot set user authentication: {}", e.getMessage(), e);
        }

        // STEP 10: Continue filter chain
        // Pass request to next filter (or controller if no more filters)
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header.
     * 
     * Authorization header format:
     *   Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
     * 
     * Process:
     *   1. Get Authorization header from request
     *   2. Check if header exists and starts with "Bearer "
     *   3. Extract token by removing "Bearer " prefix (7 characters)
     *   4. Return token or null if not found
     * 
     * Examples:
     *   "Bearer abc123" → "abc123"
     *   "bearer abc123" → null (case-sensitive!)
     *   "abc123" → null (missing "Bearer " prefix)
     *   null → null (no header)
     * 
     * @param request HTTP request
     * @return JWT token string or null if not found
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        // Get Authorization header
        String bearerToken = request.getHeader("Authorization");

        // Check if header exists and starts with "Bearer "
        // Note: This is case-sensitive! "bearer" won't work.
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            // Extract token by removing "Bearer " prefix (7 characters)
            // Example: "Bearer abc123" → "abc123"
            return bearerToken.substring(7);
        }

        // No token found
        return null;
    }
}