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
        
        // Football match (active, future)
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
        
        // Past match
        pastMatchOdds = new BettingOdds();
        pastMatchOdds.setSport("Football");
        pastMatchOdds.setHomeTeam("Barcelona");
        pastMatchOdds.setAwayTeam("Valencia");
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
}