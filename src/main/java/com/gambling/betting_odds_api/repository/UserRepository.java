package com.gambling.betting_odds_api.repository;
// Internal project imports
import com.gambling.betting_odds_api.model.User; // User entity
// Spring Data JPA - Repository abstraction for database operations
import org.springframework.data.jpa.repository.JpaRepository; // Base repository with CRUD operations
import org.springframework.stereotype.Repository;             // Marks interface as Spring Data repository
// Java Standard Library
import java.util.Optional; // Container for nullable values (avoids NullPointerException)
/**
UserRepository - Data access layer for User entity.
Spring Data JPA automatically implements this interface.
No need to write SQL queries manually!
Inherited methods from JpaRepository:
save(User) - Insert or update
findById(Long) - Find by primary key
findAll() - Get all users
deleteById(Long) - Delete by ID
count() - Count all users
Custom query methods:
Spring Data JPA generates SQL from method names:
findByUsername → SELECT * FROM users WHERE username = ?
findByEmail → SELECT * FROM users WHERE email = ?
existsByUsername → SELECT COUNT(*) > 0 FROM users WHERE username = ?
existsByEmail → SELECT COUNT(*) > 0 FROM users WHERE email = ?
*/
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
    Find user by username.
    Generated SQL:
    SELECT * FROM users WHERE username = ?
    @param username Username to search for
    @return Optional containing user if found, empty otherwise
    */
    Optional<User> findByUsername(String username);
    /**
    Find user by email.
    Generated SQL:
    SELECT * FROM users WHERE email = ?
    @param email Email to search for
    @return Optional containing user if found, empty otherwise
    */
    Optional<User> findByEmail(String email);
    /**
    Check if username exists.
    Generated SQL:
    SELECT COUNT(*) > 0 FROM users WHERE username = ?
    Used for registration validation (prevent duplicate usernames).
    @param username Username to check
    @return true if username exists, false otherwise
    */
    Boolean existsByUsername(String username);
    /**
    Check if email exists.
    Generated SQL:
    SELECT COUNT(*) > 0 FROM users WHERE email = ?
    Used for registration validation (prevent duplicate emails).
    @param email Email to check
    @return true if email exists, false otherwise
    */
    Boolean existsByEmail(String email);
}