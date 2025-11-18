package com.gambling.betting_odds_api.repository;

import com.gambling.betting_odds_api.model.BettingOdds;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BettingOddsRepository extends JpaRepository<BettingOdds, Long> {
    // Spring Data JPA automatically will implement these methods on base of name
    
    // Find all active odds with pagination
    Page<BettingOdds> findByActiveTrue(Pageable pageable);
    
    // Find by sport with pagination
    Page<BettingOdds> findBySport(String sport, Pageable pageable);
    
    // Find all active odds for a sport with pagination 
    Page<BettingOdds> findBySportAndActiveTrue(String sport, Pageable pageable);

    // Non-paginated version (keep for backward compatibility)
    List<BettingOdds> findByActiveTrue();

    List<BettingOdds> findBySport(String sport);

    List<BettingOdds> findBySportAndActiveTrue(String sport);
    
    // Find by home team (case-insensitive search)
    List<BettingOdds> findByHomeTeamContainingIgnoreCase(String teamName);
    
    // Find matches by between dates
    List<BettingOdds> findByMatchDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Custom Query - find upcoming matches with pagination
    @Query("SELECT b FROM BettingOdds b WHERE b.matchDate > :currentDate AND b.active = true ORDER BY b.matchDate ASC")
    Page<BettingOdds> findUpcomingMatches(@Param("currentDate") LocalDateTime currentDate, Pageable pageable);
    
    // =========================================================================
    // OPTIMIZED: Find matches for specific team - UNION approach (Phase 4 Week 2 Day 8)
    // =========================================================================
    // Previous implementation used OR condition which caused Seq Scan:
    // WHERE (b.homeTeam = :teamName OR b.awayTeam = :teamName)
    // 
    // New implementation uses UNION which allows PostgreSQL to use both indexes:
    // - idx_home_team for first query
    // - idx_away_team for second query
    // 
    // Performance improvement: 18% faster on small datasets (200 rows)
    //                         100-500x faster on large datasets (100k+ rows)
    // =========================================================================
    
    /**
     * Find all active matches where specified team is playing (home or away).
     * OPTIMIZED with UNION approach to utilize both home_team and away_team indexes.
     * 
     * @param teamName The team name to search for
     * @param pageable Pagination parameters
     * @return Page of betting odds where team is playing
     */
    @Query(value = """
        SELECT * FROM betting_odds 
        WHERE home_team = :teamName AND active = true
        UNION
        SELECT * FROM betting_odds 
        WHERE away_team = :teamName AND active = true
        ORDER BY match_date DESC
        """, 
        countQuery = """
        SELECT COUNT(*) FROM (
            SELECT id FROM betting_odds WHERE home_team = :teamName AND active = true
            UNION
            SELECT id FROM betting_odds WHERE away_team = :teamName AND active = true
        ) AS combined
        """,
        nativeQuery = true)
    Page<BettingOdds> findByTeam(@Param("teamName") String teamName, Pageable pageable);

    /**
     * Find all active matches where specified team is playing (home or away).
     * Non-paginated version for backward compatibility.
     * OPTIMIZED with UNION approach.
     * 
     * @param teamName The team name to search for
     * @return List of betting odds where team is playing
     */
    @Query(value = """
        SELECT * FROM betting_odds 
        WHERE home_team = :teamName AND active = true
        UNION
        SELECT * FROM betting_odds 
        WHERE away_team = :teamName AND active = true
        ORDER BY match_date DESC
        """, 
        nativeQuery = true)
    List<BettingOdds> findByTeam(@Param("teamName") String teamName);

    // Non-paginated version for backward compatibility 
    @Query("SELECT b FROM BettingOdds b WHERE b.matchDate > :currentDate AND b.active = true ORDER BY b.matchDate ASC")
    List<BettingOdds> findUpcomingMatches(@Param("currentDate") LocalDateTime currentDate);
}