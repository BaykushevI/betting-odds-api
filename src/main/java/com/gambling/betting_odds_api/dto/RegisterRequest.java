package com.gambling.betting_odds_api.dto;
// Jakarta Validation - Bean validation annotations
import jakarta.validation.constraints.Email;    // Email format validation
import jakarta.validation.constraints.NotBlank; // Not null, not empty, not whitespace
import jakarta.validation.constraints.Size;     // String length validation
// Lombok - Reduces boilerplate code
import lombok.AllArgsConstructor; // Generates constructor with all fields
import lombok.Builder;            // Generates builder pattern for object creation
import lombok.Data;               // Generates getters, setters, toString, equals, hashCode
import lombok.NoArgsConstructor;  // Generates no-args constructor
/**
RegisterRequest - DTO for user registration.
Used in POST /api/auth/register endpoint.
Contains all data needed to create a new user:
username (unique, 3-50 characters)
email (unique, valid email format)
password (minimum 6 characters for security)
Validation rules:
Username: Required, 3-50 chars, will be checked for uniqueness in service
Email: Required, valid format, will be checked for uniqueness in service
Password: Required, minimum 6 chars (will be hashed with BCrypt)
Security notes:
Password is transmitted over HTTPS (encrypted in transit)
Password will be hashed with BCrypt before storing (60 chars)
Default role is USER (can be changed by admin later)
Account is active by default
Example JSON:
{
    "username": "john",
    "email": "john@example.com",
    "password": "secret123"
}
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
}