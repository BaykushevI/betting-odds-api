package com.gambling.betting_odds_api.dto;
// Internal project imports
import com.gambling.betting_odds_api.model.Role; // User role enum
// Lombok - Reduces boilerplate code
import lombok.AllArgsConstructor; // Generates constructor with all fields
import lombok.Builder;            // Generates builder pattern for object creation
import lombok.Data;               // Generates getters, setters, toString, equals, hashCode
import lombok.NoArgsConstructor;  // Generates no-args constructor
/**
AuthResponse - DTO for authentication response.
Returned after successful login or registration.
Contains:
JWT token (for subsequent authenticated requests)
User information (username, email, role)
Token type (always "Bearer")
Client usage:
Receive this response after login/register
Store token (localStorage, sessionStorage, or memory)
Send token in Authorization header for protected endpoints:
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Security notes:
Token should be stored securely on client
Token expires after configured time (default: 24 hours)
Never expose password or sensitive data
Role is included for client-side UI decisions (not for authorization!)
Example JSON response:
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWI...",
    "tokenType": "Bearer",
    "username": "john",
    "email": "john@example.com",
    "role": "USER"
}
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
    public class AuthResponse {
    private String token;
    @Builder.Default
    private String tokenType = "Bearer";
    private String username;
    private String email;
    private Role role;
}