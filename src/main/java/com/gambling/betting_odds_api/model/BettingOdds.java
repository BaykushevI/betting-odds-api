package com.gambling.betting_odds_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity class representing betting odds for sports matches.
 * 
 * Performance Optimization (Phase 4 Week 2 Day 7):
 * - Added 5 strategic indexes for optimal query performance
 * - Composite indexes for common query patterns
 * - Single-column indexes for team searches
 * 
 * Index Strategy:
 * 1. idx_sport_active: Most common query (filter by sport + active)
 * 2. idx_match_date: Date range queries (upcoming matches)
 * 3. idx_home_team: Team search queries
 * 4. idx_away_team: Team search queries  
 * 5. idx_active_match_date: Upcoming active matches (common pattern)
 */
@Entity
@Table(name = "betting_odds", indexes = {
    // Composite index for sport + active (most common query pattern)
    // Used by: findBySport(), findBySportAndActiveTrue()
    @Index(name = "idx_sport_active", columnList = "sport, active"),
    
    // Index for match_date (date range queries, upcoming matches)
    // Used by: findUpcomingMatches(), findByMatchDateBetween()
    @Index(name = "idx_match_date", columnList = "match_date"),
    
    // Index for home_team (team search queries)
    // Used by: findByTeam(), findByHomeTeamContainingIgnoreCase()
    @Index(name = "idx_home_team", columnList = "home_team"),
    
    // Index for away_team (team search queries)
    // Used by: findByTeam()
    @Index(name = "idx_away_team", columnList = "away_team"),
    
    // Composite index for active + match_date (upcoming active matches)
    // Used by: findUpcomingMatches() - optimizes the most critical query
    @Index(name = "idx_active_match_date", columnList = "active, match_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor

public class BettingOdds {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sport; // Football, Basketball, Tennis
    
    @Column(nullable = false)
    private String homeTeam;
    
    @Column(nullable = false)
    private String awayTeam;
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal homeOdds; // Decimal odds for home win
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal drawOdds; // Decimal odds for draw
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal awayOdds; // Decimal odds for away win
    
    @Column(nullable = false)
    private LocalDateTime matchDate;
    
    @Column(nullable = false)
    private Boolean active = true; // if odds is active
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
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