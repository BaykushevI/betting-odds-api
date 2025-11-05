package com.gambling.betting_odds_api.dto;
// Jakarta Validation - Bean validation annotations
import jakarta.validation.constraints.NotBlank; // Not null, not empty, not whitespace
// Lombok - Reduces boilerplate code
import lombok.AllArgsConstructor; // Generates constructor with all fields
import lombok.Builder;            // Generates builder pattern for object creation
import lombok.Data;               // Generates getters, setters, toString, equals, hashCode
import lombok.NoArgsConstructor;  // Generates no-args constructor
/**
LoginRequest - DTO for user login.
Used in POST /api/auth/login endpoint.
Contains only the minimal data needed for authentication:
username (or email)
password (plain text - will be verified against BCrypt hash)
Security notes:
Password is transmitted over HTTPS (encrypted in transit)
Password is never stored in plain text
Password is immediately hashed and compared with stored hash
This DTO is never persisted to database
Example JSON:
{
    "username": "john",
    "password": "secret123"
}
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Password is required")
    private String password;
}