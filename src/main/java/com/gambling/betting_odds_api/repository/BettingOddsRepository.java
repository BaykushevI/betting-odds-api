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
    
    // Custom Query - find matches for specific team with pagination
    @Query("SELECT b FROM BettingOdds b WHERE (b.homeTeam = :teamName OR b.awayTeam = :teamName) AND b.active = true")
    Page<BettingOdds> findByTeam(@Param("teamName") String teamName, Pageable pageable);

    // Non-paginated version for backward compatilbility 
    @Query("SELECT b FROM BettingOdds b WHERE b.matchDate > :currentDate AND b.active = true ORDER BY b.matchDate ASC")
    List<BettingOdds> findUpcomingMatches(@Param("currentDate") LocalDateTime currentDate);
    
    // Custom Query - find matches for specific team with pagination
    @Query("SELECT b FROM BettingOdds b WHERE (b.homeTeam = :teamName OR b.awayTeam = :teamName) AND b.active = true")
    List<BettingOdds> findByTeam(@Param("teamName") String teamName);
}
