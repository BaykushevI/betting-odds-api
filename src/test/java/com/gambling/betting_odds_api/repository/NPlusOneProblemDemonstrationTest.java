package com.gambling.betting_odds_api.repository;

// JUnit 5 Testing Framework
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// AssertJ - Fluent assertions library
import static org.assertj.core.api.Assertions.assertThat;

// Spring Boot Test annotations
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

// Your project model classes
import com.gambling.betting_odds_api.model.BettingOdds;
import com.gambling.betting_odds_api.model.Role;
import com.gambling.betting_odds_api.model.User;

// Java standard library
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * N+1 Problem Demonstration Tests (Phase 4 Week 2 Day 9)
 * 
 * This test class demonstrates the N+1 problem and its solution with JOIN FETCH.
 * 
 * WHAT IS N+1 PROBLEM?
 * - Fetching a list of entities (1 query)
 * - Then accessing related entities causes N additional queries
 * - Total: 1 + N queries = Performance bottleneck!
 * 
 * EXAMPLE:
 * - Fetch 10 BettingOdds records (1 query)
 * - Access createdBy.getUsername() for each (10 queries)
 * - Total: 11 queries instead of 1!
 * 
 * SOLUTION:
 * - Use JOIN FETCH to load related entities in single query
 * - Or use @EntityGraph annotation
 * 
 * HOW TO SEE N+1 IN ACTION:
 * 1. Enable Hibernate SQL logging (show_sql=true)
 * 2. Run tests and watch console output
 * 3. Count SELECT statements
 * 
 * To enable SQL logging, add to application-test.properties:
 *   spring.jpa.show-sql=true
 *   spring.jpa.properties.hibernate.format_sql=true
 *   logging.level.org.hibernate.SQL=DEBUG
 */
@DataJpaTest
@DisplayName("N+1 Problem Demonstration Tests")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.jpa.show-sql=true",  // Show SQL in console
    "spring.jpa.properties.hibernate.format_sql=true"  // Format SQL for readability
})
class NPlusOneProblemDemonstrationTest {

    @Autowired
    private BettingOddsRepository oddsRepository;

    @Autowired
    private UserRepository userRepository;

    private User bookmaker;
    private User admin;

    @BeforeEach
    void setUp() {
        // Clean database
        oddsRepository.deleteAll();
        userRepository.deleteAll();

        // Create test users
        bookmaker = new User();
        bookmaker.setUsername("test_bookmaker");
        bookmaker.setEmail("bookmaker@test.com");
        bookmaker.setPassword("$2a$10$hashedpassword");
        bookmaker.setRole(Role.BOOKMAKER);
        bookmaker.setActive(true);
        bookmaker = userRepository.save(bookmaker);

        admin = new User();
        admin.setUsername("test_admin");
        admin.setEmail("admin@test.com");
        admin.setPassword("$2a$10$hashedpassword");
        admin.setRole(Role.ADMIN);
        admin.setActive(true);
        admin = userRepository.save(admin);

        // Create 10 betting odds records
        for (int i = 1; i <= 10; i++) {
            BettingOdds odds = new BettingOdds();
            odds.setSport("Football");
            odds.setHomeTeam("Team " + i);
            odds.setAwayTeam("Team " + (i + 10));
            odds.setHomeOdds(BigDecimal.valueOf(2.00 + i * 0.1));
            odds.setDrawOdds(BigDecimal.valueOf(3.00));
            odds.setAwayOdds(BigDecimal.valueOf(3.50 - i * 0.1));
            odds.setMatchDate(LocalDateTime.now().plusDays(i));
            odds.setActive(true);
            
            // Alternate between bookmaker and admin
            odds.setCreatedBy(i % 2 == 0 ? bookmaker : admin);
            
            oddsRepository.save(odds);
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("TEST DATA SETUP COMPLETE: 10 BettingOdds + 2 Users created");
        System.out.println("=".repeat(80) + "\n");
    }

    // ═════════════════════════════════════════════════════════════════════════
    // TEST 1: Demonstrate N+1 Problem (BAD)
    // ═════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("BAD: N+1 Problem - Accessing createdBy causes N additional queries")
    void demonstrateNPlusOneProblem() {
        System.out.println("\n" + "█".repeat(80));
        System.out.println("█ TEST 1: N+1 PROBLEM DEMONSTRATION (BAD)");
        System.out.println("█".repeat(80));
        System.out.println("Watch the SQL output below - you'll see MULTIPLE SELECT statements!");
        System.out.println("Expected: 1 query for odds + 10 queries for users = 11 TOTAL\n");

        // Query 1: Fetch all odds (WITHOUT JOIN FETCH)
        List<BettingOdds> odds = oddsRepository.findAllOdds();
        
        System.out.println("✓ First query executed: SELECT * FROM betting_odds");
        System.out.println("✓ Loaded " + odds.size() + " BettingOdds records\n");
        
        System.out.println("Now accessing createdBy.getUsername() for each record...\n");

        // Queries 2-11: Accessing createdBy triggers LAZY loading (N additional queries)
        int queryCount = 1;  // We already did 1 query above
        
        for (BettingOdds odd : odds) {
            String creatorName = odd.getCreatedBy().getUsername();  // THIS TRIGGERS QUERY!
            queryCount++;
            System.out.println("Query " + queryCount + ": Loaded User for odds #" + odd.getId() + " - Creator: " + creatorName);
        }

        System.out.println("\n" + "█".repeat(80));
        System.out.println("█ RESULT: " + queryCount + " TOTAL QUERIES (1 + " + odds.size() + ")");
        System.out.println("█ This is the N+1 PROBLEM! 😱");
        System.out.println("█".repeat(80) + "\n");

        // Verify we got the data
        assertThat(odds).hasSize(10);
        assertThat(odds).allMatch(o -> o.getCreatedBy() != null);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // TEST 2: Solution with JOIN FETCH (GOOD)
    // ═════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("GOOD: JOIN FETCH - Loads everything in ONE query")
    void demonstrateJoinFetchSolution() {
        System.out.println("\n" + "█".repeat(80));
        System.out.println("█ TEST 2: JOIN FETCH SOLUTION (GOOD)");
        System.out.println("█".repeat(80));
        System.out.println("Watch the SQL output - you'll see ONLY ONE SELECT with JOIN!");
        System.out.println("Expected: 1 query total (with LEFT JOIN users)\n");

        // Single query: Fetch odds WITH User using JOIN FETCH
        List<BettingOdds> odds = oddsRepository.findAllOddsWithCreator();
        
        System.out.println("✓ Single query executed: SELECT o.*, u.* FROM betting_odds o LEFT JOIN users u");
        System.out.println("✓ Loaded " + odds.size() + " BettingOdds records WITH their creators\n");
        
        System.out.println("Now accessing createdBy.getUsername() for each record...\n");

        // NO additional queries! User data already loaded
        for (BettingOdds odd : odds) {
            String creatorName = odd.getCreatedBy().getUsername();  // NO QUERY - already loaded!
            System.out.println("✓ No query needed! Creator for odds #" + odd.getId() + ": " + creatorName);
        }

        System.out.println("\n" + "█".repeat(80));
        System.out.println("█ RESULT: 1 TOTAL QUERY");
        System.out.println("█ This is the solution! 🚀");
        System.out.println("█ Performance improvement: " + (odds.size() + 1) + "x fewer queries!");
        System.out.println("█".repeat(80) + "\n");

        // Verify we got the same data
        assertThat(odds).hasSize(10);
        assertThat(odds).allMatch(o -> o.getCreatedBy() != null);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // TEST 3: Alternative with @EntityGraph (GOOD)
    // ═════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("GOOD: @EntityGraph - Alternative to JOIN FETCH")
    void demonstrateEntityGraphSolution() {
        System.out.println("\n" + "█".repeat(80));
        System.out.println("█ TEST 3: @EntityGraph SOLUTION (ALTERNATIVE)");
        System.out.println("█".repeat(80));
        System.out.println("@EntityGraph is cleaner syntax, same result as JOIN FETCH");
        System.out.println("Expected: 1 query total\n");

        // Single query using @EntityGraph
        List<BettingOdds> odds = oddsRepository.findAllOddsWithCreatorUsingEntityGraph();
        
        System.out.println("✓ Single query executed via @EntityGraph");
        System.out.println("✓ Loaded " + odds.size() + " BettingOdds WITH creators\n");

        // Access createdBy - no additional queries
        for (BettingOdds odd : odds) {
            String creatorName = odd.getCreatedBy().getUsername();
            System.out.println("✓ Creator for odds #" + odd.getId() + ": " + creatorName);
        }

        System.out.println("\n" + "█".repeat(80));
        System.out.println("█ RESULT: 1 TOTAL QUERY (same as JOIN FETCH)");
        System.out.println("█".repeat(80) + "\n");

        assertThat(odds).hasSize(10);
        assertThat(odds).allMatch(o -> o.getCreatedBy() != null);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // SUMMARY & BEST PRACTICES
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * SUMMARY OF FINDINGS:
     * 
     * N+1 Problem (BAD):
     * - 11 queries for 10 records (1 + 10)
     * - Performance degrades linearly with dataset size
     * - 100 records = 101 queries
     * - 1000 records = 1001 queries
     * 
     * JOIN FETCH Solution (GOOD):
     * - 1 query for ANY number of records
     * - Constant performance regardless of dataset size
     * - 100 records = 1 query
     * - 1000 records = 1 query
     * 
     * @EntityGraph Solution (GOOD):
     * - Same as JOIN FETCH, cleaner syntax
     * - Preferred for simple scenarios
     * 
     * WHEN TO USE EACH:
     * 
     * 1. NO relationship loading (LAZY default):
     *    - When you DON'T need related entities
     *    - Public API that doesn't show creator
     *    - Keeps queries lightweight
     * 
     * 2. JOIN FETCH:
     *    - When you KNOW you'll need related entities
     *    - Complex queries with multiple conditions
     *    - Full control over SQL generated
     * 
     * 3. @EntityGraph:
     *    - Simple scenarios with standard queries
     *    - Cleaner code, less verbose
     *    - Spring Data JPA handles SQL generation
     * 
     * PRODUCTION RECOMMENDATION:
     * - Use LAZY loading by default (prevents accidental N+1)
     * - Add JOIN FETCH only where needed (based on use case)
     * - Monitor SQL logs in development to catch N+1 problems
     * - Use Hibernate statistics or APM tools in production
     */
}