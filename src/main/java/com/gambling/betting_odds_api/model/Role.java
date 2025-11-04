package com.gambling.betting_odds_api.model;
/**
Role - User role enumeration for access control.
Defines three levels of access in the betting odds system:
USER - Regular user (read-only access)
Can view odds
Can search and filter
Cannot modify data
BOOKMAKER - Bookmaker/operator (read-write access)
All USER permissions
Can create odds
Can update odds
Can deactivate odds
ADMIN - System administrator (full access)
All BOOKMAKER permissions
Can delete odds (hard delete)
Can manage users
Can access admin endpoints
Spring Security Integration:
Used with @PreAuthorize("hasRole('ADMIN')")
Spring adds "ROLE_" prefix automatically
In database stored as: "USER", "BOOKMAKER", "ADMIN"
*/
    public enum Role {
        USER,
        BOOKMAKER,
        ADMIN
}