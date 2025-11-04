package com.gambling.betting_odds_api.model;
// JPA (Java Persistence API) - ORM annotations for database mapping
import jakarta.persistence.Column;           // Column configuration (unique, nullable, length)
import jakarta.persistence.Entity;           // Marks class as JPA entity (database table)
import jakarta.persistence.EnumType;         // How to store enum (STRING or ORDINAL)
import jakarta.persistence.Enumerated;       // Marks field as enum type
import jakarta.persistence.GeneratedValue;   // Auto-generate primary key value
import jakarta.persistence.GenerationType;   // Strategy for key generation (IDENTITY, SEQUENCE, etc.)
import jakarta.persistence.Id;               // Marks field as primary key
import jakarta.persistence.PrePersist;       // Callback before entity is first saved
import jakarta.persistence.PreUpdate;        // Callback before entity is updated
import jakarta.persistence.Table;            // Table configuration (name, indexes, constraints)
// Jakarta Validation - Bean validation annotations
import jakarta.validation.constraints.Email;    // Email format validation
import jakarta.validation.constraints.NotBlank; // Not null, not empty, not whitespace
import jakarta.validation.constraints.Size;     // String length validation
// Lombok - Reduces boilerplate code
import lombok.AllArgsConstructor;  // Generates constructor with all fields
import lombok.Builder;             // Generates builder pattern for object creation
import lombok.Data;                // Generates getters, setters, toString, equals, hashCode
import lombok.NoArgsConstructor;   // Generates no-args constructor (required by JPA)
// Java Standard Library
import java.time.LocalDateTime; // Date and time without timezone
/**
User - Entity representing system users.
Database Table: users
Purpose:
Store user credentials (username, email, password)
Manage user roles (USER, BOOKMAKER, ADMIN)
Track account status (active/inactive)
Audit timestamps (createdAt, updatedAt)
Security Notes:
Password is stored as BCrypt hash (never plain text!)
Email and username are unique (database constraints)
Soft delete with 'active' flag (preserve audit trail)
*/
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 3, max = 255, message = "Password must be between 3 and 255 characters")
    @Column(nullable = false, length = 255)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.USER;
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}