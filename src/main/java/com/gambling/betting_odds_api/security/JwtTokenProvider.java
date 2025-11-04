package com.gambling.betting_odds_api.security;
// JWT Library - Token creation and parsing
import io.jsonwebtoken.Claims;                    // JWT payload (claims)
import io.jsonwebtoken.Jwts;                      // JWT builder and parser
import io.jsonwebtoken.SignatureAlgorithm;        // Signing algorithms
import io.jsonwebtoken.security.Keys;             // Secure key generation
import io.jsonwebtoken.JwtException;              // JWT exceptions
// Lombok - Logging
import lombok.extern.slf4j.Slf4j; // Logger instance
// Spring Framework - Configuration and component scanning
import org.springframework.beans.factory.annotation.Value; // Inject properties
import org.springframework.stereotype.Component;            // Spring bean
// Java Standard Library
import javax.crypto.SecretKey;     // Cryptographic key
import java.nio.charset.StandardCharsets; // Character encoding
import java.util.Date;             // Date and time
import java.util.HashMap;          // Key-value pairs
import java.util.Map;              // Map interface
import java.util.function.Function; // Functional interface
/**
JwtTokenProvider - Service for generating and validating JWT tokens.
Responsibilities:
Generate JWT tokens (with username and expiration)
Validate JWT tokens (signature, expiration)
Extract claims from tokens (username, expiration date)
JWT Structure:
Header: Algorithm and token type
Payload: Claims (username, expiration, etc.)
Signature: Ensures token integrity
Security:
Tokens are signed with HMAC-SHA256
Secret key is stored in application.properties
Tokens expire after configured time (default: 24 hours)
Cannot be modified without invalidating signature
*/
@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;
    /**
    Generate JWT token for a username.
    Process:
    Create claims (payload data)
    Set subject (username)
    Set issued date (now)
    Set expiration date (now + expiration time)
    Sign with secret key
    @param username Username to include in token
    @return JWT token string
    */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }
    /**
    Create JWT token with claims and subject.
    Token structure:
    {
    "sub": "john",                    // Subject (username)
    "iat": 1516239022,                // Issued at
    "exp": 1516325422                 // Expiration
    }
    @param claims Additional claims to include
    @param subject Token subject (username)
    @return Signed JWT token
    */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        log.debug("Creating JWT token for user: {}", subject);
        log.debug("Token will expire at: {}", expiryDate);
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }
    /**
    Get signing key from secret string.
    Converts secret string to SecretKey for signing tokens.
    Uses HMAC-SHA256 algorithm.
    @return SecretKey for signing
    */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    /**
    Extract username from JWT token.
    @param token JWT token string
    @return Username (subject claim)
    */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    /**
    Extract expiration date from JWT token.
    @param token JWT token string
    @return Expiration date
    */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    /**
    Extract specific claim from JWT token.
    Generic method for extracting any claim from token.
    Uses function to specify which claim to extract.
    @param token JWT token string
    @param claimsResolver Function to extract specific claim
    @param <T> Type of claim to extract
    @return Extracted claim value
    */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    /**
    Extract all claims from JWT token.
    Parses token and extracts payload (claims).
    Verifies signature during parsing.
    @param token JWT token string
    @return All claims from token
    @throws JwtException if token is invalid or expired
    */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
    Check if token is expired.
    @param token JWT token string
    @return true if token is expired, false otherwise
    */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    /**
    Validate JWT token.
    Checks:
    Token can be parsed (valid signature)
    Username matches expected username
    Token is not expired
    @param token JWT token string
    @param username Expected username
    @return true if token is valid, false otherwise
    */
    public Boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            boolean isValid = extractedUsername.equals(username) && !isTokenExpired(token);
            if (isValid) {
                log.debug("Token validated successfully for user: {}", username);
            } else {
                log.warn("Token validation failed for user: {}", username);
            }

            return isValid;
        } catch (JwtException e) {
            log.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }
    /**
    Validate JWT token (without username check).
    Checks only:
    Token can be parsed (valid signature)
    Token is not expired
    @param token JWT token string
    @return true if token is valid, false otherwise
    */
    public Boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            boolean isExpired = isTokenExpired(token);
            if (!isExpired) {
                log.debug("Token is valid and not expired");
            } else {
                log.warn("Token has expired");
            }

            return !isExpired;
        } catch (JwtException e) {
            log.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }
}