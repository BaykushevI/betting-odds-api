package com.gambling.betting_odds_api.security;
// Internal project imports
import com.gambling.betting_odds_api.model.User;            // User entity
import com.gambling.betting_odds_api.repository.UserRepository; // User data access
// Spring Security - User details and authentication
import org.springframework.security.core.GrantedAuthority;        // User authorities/roles
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Simple authority implementation
import org.springframework.security.core.userdetails.UserDetails;  // Spring Security user interface
import org.springframework.security.core.userdetails.UserDetailsService; // Load user by username
import org.springframework.security.core.userdetails.UsernameNotFoundException; // User not found exception
import org.springframework.stereotype.Service; // Marks class as service
// Lombok - Reduces boilerplate code
import lombok.RequiredArgsConstructor; // Constructor injection for final fields
import lombok.extern.slf4j.Slf4j;      // Logger instance
// Java Standard Library
import java.util.Collection;  // Collection interface
import java.util.Collections; // Utility for collections
/**
CustomUserDetailsService - Loads user data for Spring Security authentication.
Purpose:
Bridge between our User entity and Spring Security's UserDetails interface.
Spring Security needs UserDetails to perform authentication and authorization.
Responsibilities:
Load user from database by username
Convert User entity to UserDetails object
Provide user authorities (roles) to Spring Security
Handle user not found scenarios
Integration:
Called by JwtAuthenticationFilter to load authenticated user
Used by Spring Security for authentication decisions
Provides roles for authorization (@PreAuthorize, hasRole, etc.)
UserDetails mapping:
User.username → UserDetails.getUsername()
User.password → UserDetails.getPassword()
User.role → UserDetails.getAuthorities() (e.g., ROLE_USER)
User.active → UserDetails.isEnabled()
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

// Dependency injected via constructor
private final UserRepository userRepository;

/**
 * Load user by username - Required by UserDetailsService interface.
 * 
 * Called by Spring Security during authentication process.
 * In our case, JwtAuthenticationFilter calls this to load user data.
 * 
 * Process:
 *   1. Find user in database by username
 *   2. Throw exception if user not found
 *   3. Convert User entity to UserDetails object
 *   4. Return UserDetails with username, password, roles, enabled status
 * 
 * UserDetails contains:
 *   - username: User identifier
 *   - password: Hashed password (BCrypt)
 *   - authorities: User roles (ROLE_USER, ROLE_ADMIN, etc.)
 *   - enabled: Account status (active/inactive)
 *   - accountNonExpired: true (we don't use account expiration)
 *   - credentialsNonExpired: true (we don't use password expiration)
 *   - accountNonLocked: true (we don't use account locking)
 * 
 * @param username Username to load
 * @return UserDetails object for Spring Security
 * @throws UsernameNotFoundException if user not found
 */
@Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    log.debug("Loading user by username: {}", username);

    // STEP 1: Find user in database
    // Optional.orElseThrow() throws UsernameNotFoundException if not found
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> {
                log.warn("User not found: {}", username);
                return new UsernameNotFoundException("User not found: " + username);
            });

    log.debug("User found: {} (ID: {}, Role: {}, Active: {})", 
            user.getUsername(), user.getId(), user.getRole(), user.getActive());

    // STEP 2: Convert User entity to UserDetails
    // Spring Security's User class implements UserDetails interface
    // We use the builder pattern to create UserDetails object
    return org.springframework.security.core.userdetails.User.builder()
            // Username from our User entity
            .username(user.getUsername())
            
            // Password (BCrypt hash) from our User entity
            // Spring Security will use this for password verification if needed
            .password(user.getPassword())
            
            // Authorities (roles) for authorization
            // We convert our Role enum to GrantedAuthority
            // Spring Security expects role names with "ROLE_" prefix
            // Example: Role.USER → ROLE_USER, Role.ADMIN → ROLE_ADMIN
            .authorities(getAuthorities(user))
            
            // Account status flags
            // enabled: Based on our User.active field
            // Other flags: We don't use them, so they're always true
            .disabled(!user.getActive())  // disabled = !active
            .accountExpired(false)        // We don't track account expiration
            .accountLocked(false)         // We don't track account locking
            .credentialsExpired(false)    // We don't track password expiration
            
            .build();
}

/**
 * Get user authorities (roles) for Spring Security.
 * 
 * Converts our Role enum to Spring Security's GrantedAuthority.
 * Spring Security requires role names to have "ROLE_" prefix.
 * 
 * Examples:
 *   Role.USER → ROLE_USER
 *   Role.ADMIN → ROLE_ADMIN
 *   Role.BOOKMAKER → ROLE_BOOKMAKER
 * 
 * Why "ROLE_" prefix?
 *   Spring Security's hasRole("USER") automatically adds "ROLE_" prefix.
 *   So hasRole("USER") checks for "ROLE_USER" authority.
 *   If we want to use hasAuthority("USER"), we don't need the prefix.
 * 
 * @param user User entity with role
 * @return Collection of GrantedAuthority (single authority with user's role)
 */
private Collection<? extends GrantedAuthority> getAuthorities(User user) {
    // Create a single authority from user's role
    // Format: "ROLE_" + role name (e.g., "ROLE_USER")
    String roleName = "ROLE_" + user.getRole().name();
    
    log.debug("Granting authority: {} to user: {}", roleName, user.getUsername());
    
    // Return singleton collection with the authority
    // Collections.singletonList() creates immutable list with one element
    return Collections.singletonList(new SimpleGrantedAuthority(roleName));
}
}