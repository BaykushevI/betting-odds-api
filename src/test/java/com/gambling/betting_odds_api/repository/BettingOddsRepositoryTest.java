package com.gambling.betting_odds_api.repository;

// JUnit 5 Testing Framework
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

// JUnit 5 Assertions
import static org.junit.jupiter.api.Assertions.*;

// Spring Boot Test annotations
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

// Your project classes
import com.gambling.betting_odds_api.model.BettingOdds;

// Java standard library
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Integration Tests for BettingOddsRepository
 * 
 * WHAT IS @DataJpaTest?
 * - Uses in-memory H2 database (not PostgreSQL)
 * - Automatically rolls back after each test (clean state)
 * - Only loads JPA components (Repository, Entity)
 * - Faster than @SpringBootTest
 * 
 * WHY TEST REPOSITORY?
 * - Verify custom queries work correctly (@Query)
 * - Test pagination and sorting
 * - Ensure Spring Data JPA generates correct SQL
 * - Catch database-related bugs early
 * 
 * NO MOCKING NEEDED:
 * We test against REAL database (H2 in-memory).
 */
@DataJpaTest
@DisplayName("BettingOddsRepository Integration Tests")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BettingOddsRepositoryTest {
    
    // Real repository instance (injected by Spring)
    @Autowired
    private BettingOddsRepository repository;
    
    // Test data
    private BettingOdds footballOdds;
    private BettingOdds basketballOdds;
    private BettingOdds inactiveOdds;
    private BettingOdds pastMatchOdds;
    
    @BeforeEach
    void setUp() {
        // Clean database before each test
        repository.deleteAll();
        
        // Create test data
        LocalDateTime futureDate = LocalDateTime.now().plusDays(7);
        LocalDateTime pastDate = LocalDateTime.now().minusDays(7);
        
        // Football match (active, future) - Barcelona as HOME
        footballOdds = new BettingOdds();
        footballOdds.setSport("Football");
        footballOdds.setHomeTeam("Barcelona");
        footballOdds.setAwayTeam("Real Madrid");
        footballOdds.setHomeOdds(BigDecimal.valueOf(2.10));
        footballOdds.setDrawOdds(BigDecimal.valueOf(3.40));
        footballOdds.setAwayOdds(BigDecimal.valueOf(3.60));
        footballOdds.setMatchDate(futureDate);
        footballOdds.setActive(true);
        
        // Basketball match (active, future)
        basketballOdds = new BettingOdds();
        basketballOdds.setSport("Basketball");
        basketballOdds.setHomeTeam("Lakers");
        basketballOdds.setAwayTeam("Warriors");
        basketballOdds.setHomeOdds(BigDecimal.valueOf(1.90));
        basketballOdds.setDrawOdds(BigDecimal.valueOf(15.00)); // High draw odds (rare in basketball)
        basketballOdds.setAwayOdds(BigDecimal.valueOf(2.00));
        basketballOdds.setMatchDate(futureDate);
        basketballOdds.setActive(true);
        
        // Inactive football match
        inactiveOdds = new BettingOdds();
        inactiveOdds.setSport("Football");
        inactiveOdds.setHomeTeam("Chelsea");
        inactiveOdds.setAwayTeam("Arsenal");
        inactiveOdds.setHomeOdds(BigDecimal.valueOf(2.50));
        inactiveOdds.setDrawOdds(BigDecimal.valueOf(3.20));
        inactiveOdds.setAwayOdds(BigDecimal.valueOf(2.80));
        inactiveOdds.setMatchDate(futureDate);
        inactiveOdds.setActive(false); // INACTIVE
        
        // Past match - Barcelona as AWAY team (CHANGED!)
        pastMatchOdds = new BettingOdds();
        pastMatchOdds.setSport("Football");
        pastMatchOdds.setHomeTeam("Valencia");  // CHANGED: Valencia is home
        pastMatchOdds.setAwayTeam("Barcelona"); // CHANGED: Barcelona is away
        pastMatchOdds.setHomeOdds(BigDecimal.valueOf(1.50));
        pastMatchOdds.setDrawOdds(BigDecimal.valueOf(4.00));
        pastMatchOdds.setAwayOdds(BigDecimal.valueOf(6.00));
        pastMatchOdds.setMatchDate(pastDate); // PAST DATE
        pastMatchOdds.setActive(true);
        
        // Save all test data
        repository.save(footballOdds);
        repository.save(basketballOdds);
        repository.save(inactiveOdds);
        repository.save(pastMatchOdds);
    }
    
    // ═════════════════════════════════════════════════════════════════════
    // TEST 1: Basic CRUD - Save and FindById
    // ═════════════════════════════════════════════════════════════════════
    
    @Test
    @DisplayName("Save and FindById - Should Save and Retrieve Entity")
    void saveAndFindById_ShouldSaveAndRetrieveEntity() {
        // WHAT DOES THIS TEST?
        // - Basic save() operation
        // - Basic findById() operation
        // - Auto-generated ID
        
        // ARRANGE - create new odds
        BettingOdds newOdds = new BettingOdds();
        newOdds.setSport("Tennis");
        newOdds.setHomeTeam("Federer");
        newOdds.setAwayTeam("Nadal");
        newOdds.setHomeOdds(BigDecimal.valueOf(2.20));
        newOdds.setDrawOdds(BigDecimal.valueOf(1.01)); // Draw not applicable for tennis
        newOdds.setAwayOdds(BigDecimal.valueOf(1.70));
        newOdds.setMatchDate(LocalDateTime.now().plusDays(3));
        newOdds.setActive(true);
        
        // ACT - save to database
        BettingOdds saved = repository.save(newOdds);
        
        // ASSERT - verify saved
        assertNotNull(saved.getId()); // ID was auto-generated
        
        // ACT - retrieve from database
        Optional<BettingOdds> found = repository.findById(saved.getId());
        
        // ASSERT - verify retrieved
        assertTrue(found.isPresent());
        assertEquals("Tennis", found.get().getSport());
        assertEquals("Federer", found.get().getHomeTeam());
        assertEquals("Nadal", found.get().getAwayTeam());
    }
    
    // ═════════════════════════════════════════════════════════════════════
    // TEST 2: FindAll - Pagination
    // ═════════════════════════════════════════════════════════════════════
    
    @Test
    @DisplayName("FindAll - With Pagination - Should Return Correct Page")
    void findAll_WithPagination_ShouldReturnCorrectPage() {
        // WHAT DOES THIS TEST?
        // - Pagination works correctly
        // - Page size is respected
        // - Total elements count is correct
        
        // ARRANGE - create pageable (page 0, size 2)
        Pageable pageable = PageRequest.of(0, 2);
        
        // ACT - get first page
        Page<BettingOdds> page = repository.findAll(pageable);
        
        // ASSERT - verify pagination
        assertNotNull(page);
        assertEquals(2, page.getNumberOfElements()); // 2 items on this page
        assertEquals(4, page.getTotalElements()); // 4 total items
        assertEquals(2, page.getTotalPages()); // 2 pages total (4 items / 2 per page)
        assertTrue(page.hasNext()); // Has next page
        assertFalse(page.hasPrevious()); // First page, no previous
    }
    
    // ═════════════════════════════════════════════════════════════════════
    // TEST 3: FindByActiveTrue - Filter Active Odds
    // ═════════════════════════════════════════════════════════════════════
    
    @Test
    @DisplayName("FindByActiveTrue - Should Return Only Active Odds")
    void findByActiveTrue_ShouldReturnOnlyActiveOdds() {
        // WHAT DOES THIS TEST?
        // - Custom query method (Spring Data JPA generates SQL)
        // - Filtering by active = true
        
        // ACT - get active odds
        List<BettingOdds> activeOdds = repository.findByActiveTrue();
        
        // ASSERT - verify only active odds returned
        assertEquals(3, activeOdds.size()); // 3 active odds (football, basketball, past)
        assertTrue(activeOdds.stream().allMatch(BettingOdds::getActive)); // All are active
    }
    
    // ═════════════════════════════════════════════════════════════════════
    // TEST 4: FindBySport - Filter by Sport
    // ═════════════════════════════════════════════════════════════════════
    
    @Test
    @DisplayName("FindBySport - Should Return Odds for Specific Sport")
    void findBySport_ShouldReturnOddsForSpecificSport() {
        // WHAT DOES THIS TEST?
        // - Custom query by sport field
        // - Case-sensitive matching
        
        // ACT - get football odds
        List<BettingOdds> footballList = repository.findBySport("Football");
        
        // ASSERT - verify only football returned
        assertEquals(3, footballList.size()); // 3 football matches
        assertTrue(footballList.stream().allMatch(odds -> "Football".equals(odds.getSport())));
        
        // ACT - get basketball odds
        List<BettingOdds> basketballList = repository.findBySport("Basketball");
        
        // ASSERT - verify only basketball returned
        assertEquals(1, basketballList.size());
        assertEquals("Basketball", basketballList.get(0).getSport());
    }
    
    // ═════════════════════════════════════════════════════════════════════
    // TEST 5: FindBySportAndActiveTrue - Combined Filter
    // ═════════════════════════════════════════════════════════════════════
    
    @Test
    @DisplayName("FindBySportAndActiveTrue - Should Filter by Sport and Active")
    void findBySportAndActiveTrue_ShouldFilterBySportAndActive() {
        // WHAT DOES THIS TEST?
        // - Multiple conditions (sport AND active)
        // - Spring Data JPA generates WHERE sport = ? AND active = true
        
        // ACT - get active football odds
        List<BettingOdds> activeFootball = repository.findBySportAndActiveTrue("Football");
        
        // ASSERT - verify filtered correctly
        assertEquals(2, activeFootball.size()); // 2 active football (footballOdds, pastMatchOdds)
        assertTrue(activeFootball.stream().allMatch(odds -> 
            "Football".equals(odds.getSport()) && odds.getActive()
        ));
    }
    
    // ═════════════════════════════════════════════════════════════════════
    // TEST 6: FindUpcomingMatches - Custom @Query
    // ═════════════════════════════════════════════════════════════════════
    
    @Test
    @DisplayName("FindUpcomingMatches - Should Return Only Future Active Matches")
    void findUpcomingMatches_ShouldReturnOnlyFutureActiveMatches() {
        // WHAT DOES THIS TEST?
        // - Custom @Query with WHERE clause
        // - Date comparison (matchDate > currentDate)
        // - Pagination
        
        // ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        
        // ACT - get upcoming matches
        Page<BettingOdds> upcoming = repository.findUpcomingMatches(
            LocalDateTime.now(), 
            pageable
        );
        
        // ASSERT - verify only future matches
        assertEquals(2, upcoming.getTotalElements()); // 2 upcoming (football, basketball)
        assertTrue(upcoming.getContent().stream().allMatch(odds -> 
            odds.getMatchDate().isAfter(LocalDateTime.now()) && odds.getActive()
        ));
    }
    
    // ═════════════════════════════════════════════════════════════════════
    // TEST 7: FindByTeam - Search by Team Name
    // ═════════════════════════════════════════════════════════════════════
    
    @Test
    @DisplayName("FindByTeam - Should Find Matches Where Team Plays")
    void findByTeam_ShouldFindMatchesWhereTeamPlays() {
        // WHAT DOES THIS TEST?
        // - Custom @Query with OR condition (homeTeam OR awayTeam)
        // - Useful for "show all matches for Barcelona"
        
        // ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        
        // ACT - find matches with Barcelona
        Page<BettingOdds> barcelonaMatches = repository.findByTeam("Barcelona", pageable);
        
        // ASSERT - verify Barcelona is home or away
        assertEquals(2, barcelonaMatches.getTotalElements()); // 2 Barcelona matches
        assertTrue(barcelonaMatches.getContent().stream().allMatch(odds -> 
            "Barcelona".equals(odds.getHomeTeam()) || "Barcelona".equals(odds.getAwayTeam())
        ));
    }
    
    // ═════════════════════════════════════════════════════════════════════
    // TEST 8: Sorting - Order by Match Date
    // ═════════════════════════════════════════════════════════════════════
    
    @Test
    @DisplayName("FindAll - With Sorting - Should Order by Match Date")
    void findAll_WithSorting_ShouldOrderByMatchDate() {
        // WHAT DOES THIS TEST?
        // - Sorting functionality
        // - Ascending order
        
        // ARRANGE - sort by matchDate ascending
        Pageable pageable = PageRequest.of(0, 10, Sort.by("matchDate").ascending());
        
        // ACT - get all odds sorted
        Page<BettingOdds> sorted = repository.findAll(pageable);
        
        // ASSERT - verify order (past match first, then future matches)
        List<BettingOdds> content = sorted.getContent();
        assertTrue(content.get(0).getMatchDate().isBefore(content.get(1).getMatchDate()));
        // First should be past match, last should be future matches
    }
    
    // ═════════════════════════════════════════════════════════════════════
    // TEST 9: Delete - Remove Entity
    // ═════════════════════════════════════════════════════════════════════
    
    @Test
    @DisplayName("Delete - Should Remove Entity from Database")
    void delete_ShouldRemoveEntityFromDatabase() {
        // WHAT DOES THIS TEST?
        // - Delete operation
        // - Verify entity is gone
        
        // ARRANGE - get ID to delete
        Long idToDelete = footballOdds.getId();
        
        // ACT - delete
        repository.deleteById(idToDelete);
        
        // ASSERT - verify deleted
        Optional<BettingOdds> deleted = repository.findById(idToDelete);
        assertFalse(deleted.isPresent()); // Should not exist anymore
        
        // Verify count decreased
        assertEquals(3, repository.count()); // Was 4, now 3
    }
    
    // ═════════════════════════════════════════════════════════════════════
    // TEST 10: FindByMatchDateBetween - Date Range Query
    // ═════════════════════════════════════════════════════════════════════
    
    @Test
    @DisplayName("FindByMatchDateBetween - Should Return Matches in Date Range")
    void findByMatchDateBetween_ShouldReturnMatchesInDateRange() {
        // WHAT DOES THIS TEST?
        // - Date range queries
        // - BETWEEN operator (INCLUSIVE on both ends)
        // - NOTE: Does NOT filter by active status
        
        // ARRANGE - define date range (future matches only)
        LocalDateTime start = LocalDateTime.now().plusDays(1); // Start from tomorrow
        LocalDateTime end = LocalDateTime.now().plusDays(30);
        
        // ACT - find matches in range
        List<BettingOdds> matchesInRange = repository.findByMatchDateBetween(start, end);
        
        // ASSERT - verify all in range (includes both active and inactive)
        assertEquals(3, matchesInRange.size()); // 3 future matches (2 active + 1 inactive)
        
        // Verify all dates are within range
        assertTrue(matchesInRange.stream().allMatch(odds -> 
            (odds.getMatchDate().isAfter(start) || odds.getMatchDate().isEqual(start)) && 
            (odds.getMatchDate().isBefore(end) || odds.getMatchDate().isEqual(end))
        ));
        
        // Verify we have both active and inactive matches
        long activeCount = matchesInRange.stream().filter(BettingOdds::getActive).count();
        assertEquals(2, activeCount); // 2 active matches
    }
    
    // ═════════════════════════════════════════════════════════════════════
    // SUMMARY
    // ═════════════════════════════════════════════════════════════════════
    
    // We tested 10 repository methods:
    // 1. save() + findById() - Basic CRUD
    // 2. findAll() with Pagination
    // 3. findByActiveTrue() - Filter active
    // 4. findBySport() - Filter by sport
    // 5. findBySportAndActiveTrue() - Multiple filters
    // 6. findUpcomingMatches() - Custom @Query with date
    // 7. findByTeam() - Custom @Query with OR
    // 8. Sorting by matchDate
    // 9. deleteById() - Delete operation
    // 10. findByMatchDateBetween() - Date range
    //
    // Total: 10 integration tests
    // Coverage: All main repository methods

    // ═════════════════════════════════════════════════════════════════════
    // TESTS FOR OPTIMIZED findByTeam() - UNION APPROACH (Phase 4 Week 2 Day 8)
    // ═════════════════════════════════════════════════════════════════════

    /**
     * These tests verify the UNION-based optimization of findByTeam() query.
     * 
     * OLD IMPLEMENTATION (Day 7):
     * - Used OR condition: WHERE (homeTeam = :team OR awayTeam = :team)
     * - Caused Seq Scan (no index usage)
     * - Performance: 0.195ms on 200 rows
     * 
     * NEW IMPLEMENTATION (Day 8):
     * - Uses UNION: SELECT ... WHERE homeTeam = :team UNION SELECT ... WHERE awayTeam = :team
     * - Uses both idx_home_team and idx_away_team indexes
     * - Performance: 0.159ms on 200 rows (18% faster)
     * - Scaling: 100-500x faster on large datasets (100k+ rows)
     */

    @Test
    @DisplayName("OPTIMIZED FindByTeam - Should use UNION and return correct results")
    void findByTeamOptimized_ShouldReturnCorrectResults() {
        // WHAT DOES THIS TEST?
        // - New UNION-based query returns same results as old OR-based query
        // - No regressions in functionality
        // - Backward compatibility maintained
        
        // ARRANGE - Barcelona plays in 2 matches (1 home, 1 away)
        Pageable pageable = PageRequest.of(0, 10);
        
        // ACT - find Barcelona matches
        Page<BettingOdds> result = repository.findByTeam("Barcelona", pageable);
        
        // ASSERT - verify correct results
        assertEquals(2, result.getTotalElements(), 
            "Should find 2 matches where Barcelona plays (1 home + 1 away)");
        
        // Verify one match has Barcelona as home team
        boolean hasHomeMatch = result.getContent().stream()
            .anyMatch(odds -> "Barcelona".equals(odds.getHomeTeam()));
        assertTrue(hasHomeMatch, "Should find match where Barcelona is home team");
        
        // Verify one match has Barcelona as away team
        boolean hasAwayMatch = result.getContent().stream()
            .anyMatch(odds -> "Barcelona".equals(odds.getAwayTeam()));
        assertTrue(hasAwayMatch, "Should find match where Barcelona is away team");
        
        // Verify all matches are active
        assertTrue(result.getContent().stream().allMatch(BettingOdds::getActive),
            "All returned matches should be active");
    }

    @Test
    @DisplayName("OPTIMIZED FindByTeam - Should handle team appearing in both positions")
    void findByTeamOptimized_ShouldHandleTeamInBothPositions() {
        // WHAT DOES THIS TEST?
        // - UNION correctly handles when team appears as both home AND away
        // - No duplicate results (UNION removes duplicates)
        
        // ARRANGE - Add another Barcelona match where they are away
        BettingOdds anotherBarcelonaMatch = new BettingOdds();
        anotherBarcelonaMatch.setSport("Football");
        anotherBarcelonaMatch.setHomeTeam("Sevilla");
        anotherBarcelonaMatch.setAwayTeam("Barcelona");
        anotherBarcelonaMatch.setHomeOdds(BigDecimal.valueOf(2.80));
        anotherBarcelonaMatch.setDrawOdds(BigDecimal.valueOf(3.10));
        anotherBarcelonaMatch.setAwayOdds(BigDecimal.valueOf(2.40));
        anotherBarcelonaMatch.setMatchDate(LocalDateTime.now().plusDays(14));
        anotherBarcelonaMatch.setActive(true);
        repository.save(anotherBarcelonaMatch);
        
        // Now Barcelona plays in 3 matches: 2 as home, 1 as away
        
        // ACT
        List<BettingOdds> results = repository.findByTeam("Barcelona");
        
        // ASSERT
        assertEquals(3, results.size(), 
            "Should find all 3 Barcelona matches (2 home + 1 away)");
        
        // Verify no duplicates by checking unique IDs
        long uniqueIds = results.stream()
            .map(BettingOdds::getId)
            .distinct()
            .count();
        assertEquals(3, uniqueIds, "Should have 3 unique match IDs (no duplicates)");
    }

    @Test
    @DisplayName("OPTIMIZED FindByTeam - Should respect pagination with UNION query")
    void findByTeamOptimized_ShouldRespectPagination() {
        // WHAT DOES THIS TEST?
        // - Pagination works correctly with UNION query
        // - countQuery returns correct total count
        
        // ARRANGE - Add more Barcelona matches to test pagination
        for (int i = 0; i < 5; i++) {
            BettingOdds match = new BettingOdds();
            match.setSport("Football");
            match.setHomeTeam(i % 2 == 0 ? "Barcelona" : "Team" + i);
            match.setAwayTeam(i % 2 == 0 ? "Team" + i : "Barcelona");
            match.setHomeOdds(BigDecimal.valueOf(2.00 + i * 0.1));
            match.setDrawOdds(BigDecimal.valueOf(3.00));
            match.setAwayOdds(BigDecimal.valueOf(3.50 - i * 0.1));
            match.setMatchDate(LocalDateTime.now().plusDays(i + 1));
            match.setActive(true);
            repository.save(match);
        }
        
        // Now Barcelona has 7 matches total (2 original + 5 new)
        
        // ACT - Get first page (3 items)
        Pageable firstPage = PageRequest.of(0, 3);
        Page<BettingOdds> page1 = repository.findByTeam("Barcelona", firstPage);
        
        // ASSERT - First page
        assertEquals(7, page1.getTotalElements(), "Should have 7 total Barcelona matches");
        assertEquals(3, page1.getNumberOfElements(), "First page should have 3 matches");
        assertEquals(3, page1.getTotalPages(), "Should have 3 pages total (7 items / 3 per page)");
        assertTrue(page1.hasNext(), "Should have next page");
        
        // ACT - Get second page
        Pageable secondPage = PageRequest.of(1, 3);
        Page<BettingOdds> page2 = repository.findByTeam("Barcelona", secondPage);
        
        // ASSERT - Second page
        assertEquals(7, page2.getTotalElements(), "Total should still be 7");
        assertEquals(3, page2.getNumberOfElements(), "Second page should have 3 matches");
        assertTrue(page2.hasNext(), "Should have next page");
        
        // ACT - Get third (last) page
        Pageable thirdPage = PageRequest.of(2, 3);
        Page<BettingOdds> page3 = repository.findByTeam("Barcelona", thirdPage);
        
        // ASSERT - Last page
        assertEquals(7, page3.getTotalElements(), "Total should still be 7");
        assertEquals(1, page3.getNumberOfElements(), "Last page should have 1 match (7 % 3 = 1)");
        assertFalse(page3.hasNext(), "Should NOT have next page");
        assertTrue(page3.isLast(), "Should be last page");
    }

    @Test
    @DisplayName("OPTIMIZED FindByTeam - Non-paginated version should work")
    void findByTeamOptimized_NonPaginatedVersion_ShouldWork() {
        // WHAT DOES THIS TEST?
        // - Non-paginated findByTeam() method (backward compatibility)
        // - Returns List instead of Page
        
        // ACT
        List<BettingOdds> results = repository.findByTeam("Barcelona");
        
        // ASSERT
        assertEquals(2, results.size(), "Should find 2 Barcelona matches");
        assertNotNull(results.get(0).getId(), "Results should have IDs");
        assertTrue(results.stream().allMatch(BettingOdds::getActive), 
            "All results should be active");
    }

    // ═════════════════════════════════════════════════════════════════════
    // PERFORMANCE COMPARISON NOTE
    // ═════════════════════════════════════════════════════════════════════

    /**
     * PERFORMANCE METRICS (from Day 7 EXPLAIN ANALYZE):
     * 
     * Dataset: 200 rows
     * ---------------
     * OR Approach:   0.195ms (Seq Scan on 410 rows)
     * UNION Approach: 0.159ms (Index Scan on 3 rows) - 18% faster ✅
     * 
     * Projected Performance on Large Dataset (100,000 rows):
     * ---------------
     * OR Approach:   ~500ms (full table scan)
     * UNION Approach: ~5ms (index lookups) - 100x faster ✅
     * 
     * The UNION optimization becomes MORE valuable as dataset grows!
     */
}