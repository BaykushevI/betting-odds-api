package com.gambling.betting_odds_api.model;

// ═══════════════════════════════════════════════════════════════════════════
// IMPORTS - Organized by category for clarity
// ═══════════════════════════════════════════════════════════════════════════

// Jakarta Persistence (JPA) - Database mapping annotations
import jakarta.persistence.Column;           // Maps Java field to database column
import jakarta.persistence.Entity;           // Marks class as JPA entity (database table)
import jakarta.persistence.FetchType;        // LAZY vs EAGER loading strategy for relationships
import jakarta.persistence.GeneratedValue;   // Auto-generate primary key values
import jakarta.persistence.GenerationType;   // ID generation strategy (IDENTITY, SEQUENCE, etc.)
import jakarta.persistence.Id;               // Marks field as primary key
import jakarta.persistence.Index;            // Database index definition for performance
import jakarta.persistence.JoinColumn;       // Specifies foreign key column for relationships
import jakarta.persistence.ManyToOne;        // Many-to-One relationship annotation
import jakarta.persistence.PrePersist;       // Callback executed before INSERT operation
import jakarta.persistence.PreUpdate;        // Callback executed before UPDATE operation
import jakarta.persistence.Table;            // Maps entity to specific database table

// Lombok - Reduces boilerplate code (getters, setters, constructors)
import lombok.AllArgsConstructor;   // Generates constructor with all fields as parameters
import lombok.Data;                 // Generates getters, setters, toString, equals, hashCode
import lombok.NoArgsConstructor;    // Generates no-argument constructor (required by JPA)

// Java Standard Library
import java.math.BigDecimal;        // Precise decimal numbers (essential for financial calculations)
import java.time.LocalDateTime;     // Date and time without timezone (Java 8+ Time API)

/**
 * Entity class representing betting odds for sports matches.
 * 
 * This entity stores all information about betting odds offered by bookmakers,
 * including the match details, odds values, and metadata.
 * 
 * ═══════════════════════════════════════════════════════════════════════════
 * PERFORMANCE OPTIMIZATIONS
 * ═══════════════════════════════════════════════════════════════════════════
 * 
 * Phase 4 Week 2 Day 7 - Database Indexes:
 * - Added 5 strategic indexes for optimal query performance
 * - Composite indexes for common query patterns (sport+active, active+match_date)
 * - Single-column indexes for team searches
 * 
 * Index Strategy:
 * 1. idx_sport_active: Filters by sport AND active status (most common pattern)
 * 2. idx_match_date: Date range queries for upcoming matches
 * 3. idx_home_team: Fast lookups by home team name
 * 4. idx_away_team: Fast lookups by away team name
 * 5. idx_active_match_date: Optimized for "upcoming active matches" query
 * 
 * Phase 4 Week 2 Day 9 - N+1 Problem Prevention:
 * - Added @ManyToOne relationship to User (createdBy field)
 * - Uses LAZY loading by default to prevent automatic User queries
 * - Repository methods use JOIN FETCH when User data is needed
 * - Prevents N+1 problem: 1 query with JOIN instead of 1+N separate queries
 * 
 * ═══════════════════════════════════════════════════════════════════════════
 */
@Entity
@Table(name = "betting_odds", indexes = {
    // Composite index: sport + active (most frequently used filter combination)
    // Speeds up queries like: findBySport(), findBySportAndActiveTrue()
    @Index(name = "idx_sport_active", columnList = "sport, active"),
    
    // Single index: match_date (for date range queries and sorting)
    // Speeds up queries like: findUpcomingMatches(), findByMatchDateBetween()
    @Index(name = "idx_match_date", columnList = "match_date"),
    
    // Single index: home_team (for team-based searches)
    // Speeds up queries like: findByTeam(), findByHomeTeamContainingIgnoreCase()
    @Index(name = "idx_home_team", columnList = "home_team"),
    
    // Single index: away_team (for team-based searches)
    // Speeds up queries like: findByTeam() with UNION approach
    @Index(name = "idx_away_team", columnList = "away_team"),
    
    // Composite index: active + match_date (optimized for upcoming active matches)
    // Speeds up queries like: findUpcomingMatches() - the most critical query
    @Index(name = "idx_active_match_date", columnList = "active, match_date")
})
@Data                   // Lombok: generates getters, setters, toString, equals, hashCode
@NoArgsConstructor      // Lombok: generates no-argument constructor (required by JPA)
@AllArgsConstructor     // Lombok: generates constructor with all fields
public class BettingOdds {
    
    // ═════════════════════════════════════════════════════════════════════════
    // PRIMARY KEY
    // ═════════════════════════════════════════════════════════════════════════
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment in database
    private Long id;

    // ═════════════════════════════════════════════════════════════════════════
    // MATCH INFORMATION
    // ═════════════════════════════════════════════════════════════════════════
    
    @Column(nullable = false)
    private String sport; // Football, Basketball, Tennis, etc.
    
    @Column(nullable = false)
    private String homeTeam;  // Name of the home team
    
    @Column(nullable = false)
    private String awayTeam;  // Name of the away team
    
    @Column(nullable = false)
    private LocalDateTime matchDate;  // When the match will take place
    
    // ═════════════════════════════════════════════════════════════════════════
    // ODDS VALUES
    // ═════════════════════════════════════════════════════════════════════════
    // Using BigDecimal for financial precision (never use float/double for money!)
    // precision=5, scale=2 means: max 999.99
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal homeOdds; // Decimal odds for home team win (e.g., 2.10)
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal drawOdds; // Decimal odds for draw (e.g., 3.40)
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal awayOdds; // Decimal odds for away team win (e.g., 3.60)
    
    // ═════════════════════════════════════════════════════════════════════════
    // STATUS FLAGS
    // ═════════════════════════════════════════════════════════════════════════
    
    @Column(nullable = false)
    private Boolean active = true; // Whether these odds are still available for betting
    
    // ═════════════════════════════════════════════════════════════════════════
    // RELATIONSHIP: Many-to-One with User (Phase 4 Week 2 Day 9)
    // ═════════════════════════════════════════════════════════════════════════
    /**
     * The user (BOOKMAKER or ADMIN) who created these betting odds.
     * 
     * RELATIONSHIP TYPE: Many-to-One
     * - Many BettingOdds records can be created by one User
     * - One User can create many BettingOdds records
     * 
     * FETCH STRATEGY: LAZY (default and recommended)
     * - User is NOT loaded automatically when fetching BettingOdds
     * - User is only loaded when explicitly accessed: odd.getCreatedBy()
     * - This prevents N+1 problem by default
     * 
     * N+1 PROBLEM EXAMPLE (BAD - causes 1 + N database queries):
     * 
     *   List<BettingOdds> odds = repository.findAll();     // Query 1: SELECT * FROM betting_odds
     *   for (BettingOdds odd : odds) {
     *       String creator = odd.getCreatedBy().getUsername();  // Query 2-N: SELECT * FROM users WHERE id = ?
     *   }
     *   // Result: If you have 100 odds, this executes 101 queries! (1 + 100)
     * 
     * SOLUTION WITH JOIN FETCH (GOOD - only 1 database query):
     * 
     *   @Query("SELECT o FROM BettingOdds o JOIN FETCH o.createdBy")
     *   List<BettingOdds> findAllWithCreator();
     *   // Result: Only 1 query with JOIN, all data loaded at once!
     * 
     * WHEN TO USE JOIN FETCH:
     * - When you KNOW you'll need the User data (e.g., displaying creator username)
     * - API endpoints that return odds with creator information
     * - Reports and analytics that aggregate by creator
     * 
     * WHEN NOT TO USE JOIN FETCH:
     * - When you DON'T need User data (e.g., public odds display without creator)
     * - Keeps queries lightweight and fast
     */
    @ManyToOne(fetch = FetchType.LAZY)  // LAZY = User not loaded automatically
    @JoinColumn(
        name = "created_by_user_id",    // Foreign key column name in betting_odds table
        nullable = true                  // Nullable for backward compatibility with existing data
    )
    private User createdBy;  // Reference to User entity (THIS is why we need the import!)
    
    // ═════════════════════════════════════════════════════════════════════════
    // AUDIT TIMESTAMPS
    // ═════════════════════════════════════════════════════════════════════════
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;  // When this record was created (never changes)
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;  // When this record was last modified
    
    // ═════════════════════════════════════════════════════════════════════════
    // JPA LIFECYCLE CALLBACKS
    // ═════════════════════════════════════════════════════════════════════════
    
    /**
     * Called automatically by JPA before INSERT operation.
     * Sets both createdAt and updatedAt to current timestamp.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Called automatically by JPA before UPDATE operation.
     * Updates only the updatedAt timestamp (createdAt remains unchanged).
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}