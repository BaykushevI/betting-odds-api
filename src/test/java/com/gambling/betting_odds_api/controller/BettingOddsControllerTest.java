package com.gambling.betting_odds_api.controller;

// JUnit 5 Testing Framework
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

// Spring Boot Test annotations
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

// Static imports for MockMvc
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThan;

// Your project classes
import com.gambling.betting_odds_api.repository.BettingOddsRepository;
import com.gambling.betting_odds_api.repository.UserRepository;
import com.gambling.betting_odds_api.model.BettingOdds;
import com.gambling.betting_odds_api.model.User;
import com.gambling.betting_odds_api.model.Role;
import com.gambling.betting_odds_api.security.JwtTokenProvider;

// Spring Security
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// Java standard library
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
Integration Tests for BettingOddsController WITH JWT AUTHENTICATION

PHASE 3 WEEK 3 DAY 10 - JWT Authentication Integration

WHAT CHANGED FROM PREVIOUS VERSION?
1. Added JWT token generation for different user roles
2. All requests now include Authorization header with JWT token
3. Added new tests for role-based access control (403 Forbidden)
4. Created helper methods for test users and token generation

NEW DEPENDENCIES:
- UserRepository: Create test users with different roles
- JwtTokenProvider: Generate JWT tokens for authentication
- BCryptPasswordEncoder: Hash passwords for test users

TEST USER ROLES:
- userToken: Regular USER (read-only access)
- bookmakerToken: BOOKMAKER (read + create + update)
- adminToken: ADMIN (full access including delete)

AUTHENTICATION FLOW IN TESTS:
1. @BeforeEach creates test users with different roles
2. Generate JWT tokens for each user
3. Add Authorization header to MockMvc requests
4. Verify responses (200 OK for authorized, 403 for unauthorized)

WHAT IS @SpringBootTest?
- Loads FULL Spring application context (all beans)
- Uses real database (H2 in-memory for tests)
- Real HTTP requests via MockMvc
- Slower than @WebMvcTest but tests EVERYTHING

WHAT IS MockMvc?
- Simulates HTTP requests without starting real server
- Tests REST endpoints end-to-end
- Verifies status codes, headers, JSON responses

WHY @Transactional?
- Each test rolls back automatically
- Database stays clean between tests
- No need for manual cleanup

TEST STRUCTURE:
1. Setup test data and JWT tokens in @BeforeEach
2. Make HTTP request via MockMvc with Authorization header
3. Verify response (status, JSON, headers)
4. For authorization tests, verify 403 Forbidden responses
*/
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("BettingOddsController Integration Tests with JWT Authentication")
public class BettingOddsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private BettingOddsRepository bettingOddsRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Test data
    private BettingOdds testOdds;
    private LocalDateTime futureDate;
    
    // JWT tokens for different user roles
    private String userToken;        // USER role (read-only)
    private String bookmakerToken;   // BOOKMAKER role (read + create + update)
    private String adminToken;       // ADMIN role (full access)

    /**
     * Setup method - Runs before EACH test.
     * 
     * Process:
     *   1. Clean databases (betting_odds and users)
     *   2. Create test users with different roles (USER, BOOKMAKER, ADMIN)
     *   3. Generate JWT tokens for each user
     *   4. Create test betting odds data
     * 
     * Why we need different roles:
     *   - USER: Can only read (GET endpoints)
     *   - BOOKMAKER: Can read + create + update (GET, POST, PUT, PATCH)
     *   - ADMIN: Can do everything including delete (GET, POST, PUT, PATCH, DELETE)
     * 
     * Authentication in tests:
     *   - All requests must include: .header("Authorization", "Bearer " + token)
     *   - Without token: 401 Unauthorized
     *   - With wrong role: 403 Forbidden
     *   - With correct role: 200 OK
     * 
     * IMPORTANT FIX:
     *   - Using timestamp suffix to create unique usernames per test execution
     *   - Prevents "duplicate key value violates unique constraint" errors
     *   - @Transactional rollback doesn't always work with test users in PostgreSQL
     */
    @BeforeEach
    void setUp() {
        // STEP 1: Clean databases
        // Why: Ensure clean state for each test
        bettingOddsRepository.deleteAll();
        userRepository.deleteAll();
        
        // STEP 2: Create test users with different roles
        // Why: We need to test role-based access control (@PreAuthorize)
        
        // Generate unique suffix to prevent duplicate key constraint violations
        // Uses current timestamp to ensure uniqueness across test runs
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());
        
        // Create USER (read-only access)
        User regularUser = User.builder()
                .username("testuser_" + uniqueSuffix)
                .email("user_" + uniqueSuffix + "@test.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.USER)
                .active(true)
                .build();
        User savedUser = userRepository.save(regularUser);
        
        // Create BOOKMAKER (read + create + update)
        User bookmakerUser = User.builder()
                .username("bookmaker_" + uniqueSuffix)
                .email("bookmaker_" + uniqueSuffix + "@test.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.BOOKMAKER)
                .active(true)
                .build();
        User savedBookmaker = userRepository.save(bookmakerUser);
        
        // Create ADMIN (full access)
        User adminUser = User.builder()
                .username("admin_" + uniqueSuffix)
                .email("admin_" + uniqueSuffix + "@test.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.ADMIN)
                .active(true)
                .build();
        User savedAdmin = userRepository.save(adminUser);
        
        // STEP 3: Generate JWT tokens for each user
        // Why: Tests need valid tokens to access protected endpoints
        // Token format: "Bearer eyJhbGciOiJIUzI1NiJ9..."
        // IMPORTANT: Use the actual saved usernames (with suffix)
        userToken = jwtTokenProvider.generateToken(savedUser.getUsername());
        bookmakerToken = jwtTokenProvider.generateToken(savedBookmaker.getUsername());
        adminToken = jwtTokenProvider.generateToken(savedAdmin.getUsername());
        
        // STEP 4: Create test betting odds data
        futureDate = LocalDateTime.now().plusDays(7);
        
        testOdds = new BettingOdds();
        testOdds.setSport("Football");
        testOdds.setHomeTeam("Barcelona");
        testOdds.setAwayTeam("Real Madrid");
        testOdds.setHomeOdds(BigDecimal.valueOf(2.10));
        testOdds.setDrawOdds(BigDecimal.valueOf(3.40));
        testOdds.setAwayOdds(BigDecimal.valueOf(3.60));
        testOdds.setMatchDate(futureDate);
        testOdds.setActive(true);
        
        testOdds = bettingOddsRepository.save(testOdds);
    }

    // ═════════════════════════════════════════════════════════════════════
    // EXISTING TESTS - UPDATED WITH JWT AUTHENTICATION
    // ═════════════════════════════════════════════════════════════════════

    // ═════════════════════════════════════════════════════════════════════
    // TEST 1: POST /api/odds - Create New Odds (Happy Path) - WITH JWT
    // ═════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("POST /api/odds - Valid Request with BOOKMAKER token - Should Return 201 Created")
    void createOdds_ValidRequestWithBookmakerToken_ShouldReturn201Created() throws Exception {
        // WHAT CHANGED?
        // - Added .header("Authorization", "Bearer " + bookmakerToken)
        // - BOOKMAKER role is required for POST /api/odds
        // - USER token would result in 403 Forbidden
        
        // ARRANGE - create JSON request body
        String jsonRequest = """
            {
                "sport": "Basketball",
                "homeTeam": "Lakers",
                "awayTeam": "Warriors",
                "homeOdds": 1.90,
                "drawOdds": 15.00,
                "awayOdds": 2.00,
                "matchDate": "%s"
            }
            """.formatted(futureDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        // ACT & ASSERT
        mockMvc.perform(post("/api/odds")
                .header("Authorization", "Bearer " + bookmakerToken)  // NEW: JWT authentication
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.sport").value("Basketball"))
                .andExpect(jsonPath("$.homeTeam").value("Lakers"))
                .andExpect(jsonPath("$.awayTeam").value("Warriors"))
                .andExpect(jsonPath("$.homeOdds").value(1.90))
                .andExpect(jsonPath("$.drawOdds").value(15.00))
                .andExpect(jsonPath("$.awayOdds").value(2.00))
                .andExpect(jsonPath("$.active").value(true));
    }

    // ═════════════════════════════════════════════════════════════════════
    // TEST 2: POST /api/odds - Invalid Request (Missing Fields) - WITH JWT
    // ═════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("POST /api/odds - Missing Required Fields with BOOKMAKER token - Should Return 400 Bad Request")
    void createOdds_MissingFieldsWithBookmakerToken_ShouldReturn400BadRequest() throws Exception {
        // WHAT CHANGED?
        // - Added .header("Authorization", "Bearer " + bookmakerToken)
        // - Authentication still required even for invalid requests
        
        // ARRANGE - invalid JSON (missing homeTeam)
        String jsonRequest = """
            {
                "sport": "Football",
                "awayTeam": "Real Madrid",
                "homeOdds": 2.10,
                "drawOdds": 3.40,
                "awayOdds": 3.60,
                "matchDate": "%s"
            }
            """.formatted(futureDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        // ACT & ASSERT
        mockMvc.perform(post("/api/odds")
                .header("Authorization", "Bearer " + bookmakerToken)  // NEW: JWT authentication
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Invalid input data")));
    }

    // ═════════════════════════════════════════════════════════════════════
    // TEST 3: GET /api/odds/{id} - Find By ID (Happy Path) - WITH JWT
    // ═════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("GET /api/odds/{id} - Existing ID with USER token - Should Return 200 OK")
    void getOddsById_ExistingIdWithUserToken_ShouldReturn200OK() throws Exception {
        // WHAT CHANGED?
        // - Added .header("Authorization", "Bearer " + userToken)
        // - USER role can access GET endpoints (read-only)
        
        // ACT & ASSERT
        mockMvc.perform(get("/api/odds/{id}", testOdds.getId())
                .header("Authorization", "Bearer " + userToken))  // NEW: JWT authentication
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testOdds.getId()))
                .andExpect(jsonPath("$.sport").value("Football"))
                .andExpect(jsonPath("$.homeTeam").value("Barcelona"))
                .andExpect(jsonPath("$.awayTeam").value("Real Madrid"))
                .andExpect(jsonPath("$.homeOdds").value(2.10))
                .andExpect(jsonPath("$.active").value(true));
    }

    // ═════════════════════════════════════════════════════════════════════
    // TEST 4: GET /api/odds/{id} - Non-Existing ID - WITH JWT
    // ═════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("GET /api/odds/{id} - Non-Existing ID with USER token - Should Return 404 Not Found")
    void getOddsById_NonExistingIdWithUserToken_ShouldReturn404NotFound() throws Exception {
        // WHAT CHANGED?
        // - Added .header("Authorization", "Bearer " + userToken)
        
        // ARRANGE - use non-existing ID
        Long nonExistingId = 99999L;
        
        // ACT & ASSERT
        mockMvc.perform(get("/api/odds/{id}", nonExistingId)
                .header("Authorization", "Bearer " + userToken))  // NEW: JWT authentication
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("not found")));
    }

    // ═════════════════════════════════════════════════════════════════════
    // TEST 5: GET /api/odds - Pagination - WITH JWT
    // ═════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("GET /api/odds - Pagination with USER token - Should Return Paged Results")
    void getAllOdds_WithPaginationAndUserToken_ShouldReturnPagedResults() throws Exception {
        // WHAT CHANGED?
        // - Added .header("Authorization", "Bearer " + userToken)
        
        // ACT & ASSERT
        // Note: PageResponse uses "pageSize" field (not "size")
        // PageResponse structure:
        // {
        //   "content": [...],
        //   "pageNumber": 0,
        //   "pageSize": 10,      ← Correct field name
        //   "totalElements": 1,
        //   "totalPages": 1,
        //   "first": true,
        //   "last": true,
        //   "empty": false
        // }
        mockMvc.perform(get("/api/odds")
                .header("Authorization", "Bearer " + userToken)  // NEW: JWT authentication
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.pageSize").value(10))      // FIXED: pageSize not size
                .andExpect(jsonPath("$.pageNumber").value(0));     // ADDED: pageNumber assertion
    }

    // ═════════════════════════════════════════════════════════════════════
    // TEST 6: PUT /api/odds/{id} - Update Odds - WITH JWT
    // ═════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("PUT /api/odds/{id} - Valid Update with BOOKMAKER token - Should Return 200 OK")
    void updateOdds_ValidRequestWithBookmakerToken_ShouldReturn200OK() throws Exception {
        // WHAT CHANGED?
        // - Added .header("Authorization", "Bearer " + bookmakerToken)
        // - BOOKMAKER role is required for PUT /api/odds/{id}
        
        // ARRANGE - create update JSON
        String jsonRequest = """
            {
                "sport": "Football",
                "homeTeam": "Barcelona",
                "awayTeam": "Real Madrid",
                "homeOdds": 2.20,
                "drawOdds": 3.50,
                "awayOdds": 3.70,
                "matchDate": "%s",
                "active": true
            }
            """.formatted(futureDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        // ACT & ASSERT
        mockMvc.perform(put("/api/odds/{id}", testOdds.getId())
                .header("Authorization", "Bearer " + bookmakerToken)  // NEW: JWT authentication
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testOdds.getId()))
                .andExpect(jsonPath("$.homeOdds").value(2.20))
                .andExpect(jsonPath("$.drawOdds").value(3.50))
                .andExpect(jsonPath("$.awayOdds").value(3.70));
    }

    // ═════════════════════════════════════════════════════════════════════
    // TEST 7: PATCH /api/odds/{id}/deactivate - Deactivate Odds - WITH JWT
    // ═════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("PATCH /api/odds/{id}/deactivate - Valid ID with BOOKMAKER token - Should Return 200 OK")
    void deactivateOdds_ValidIdWithBookmakerToken_ShouldReturn200OK() throws Exception {
        // WHAT CHANGED?
        // - Added .header("Authorization", "Bearer " + bookmakerToken)
        // - BOOKMAKER role is required for PATCH /api/odds/{id}/deactivate
        
        // ACT & ASSERT
        mockMvc.perform(patch("/api/odds/{id}/deactivate", testOdds.getId())
                .header("Authorization", "Bearer " + bookmakerToken))  // NEW: JWT authentication
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(containsString("deactivated")));
    }

    // ═════════════════════════════════════════════════════════════════════
    // TEST 8: DELETE /api/odds/{id} - Delete Odds (Hard Delete) - WITH JWT
    // ═════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("DELETE /api/odds/{id} - Valid ID with ADMIN token - Should Return 200 OK")
    void deleteOdds_ValidIdWithAdminToken_ShouldReturn200OK() throws Exception {
        // WHAT CHANGED?
        // - Added .header("Authorization", "Bearer " + adminToken)
        // - ADMIN role is REQUIRED for DELETE /api/odds/{id}
        // - BOOKMAKER token would result in 403 Forbidden
        
        // ACT & ASSERT
        mockMvc.perform(delete("/api/odds/{id}", testOdds.getId())
                .header("Authorization", "Bearer " + adminToken))  // NEW: JWT authentication (ADMIN only!)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(containsString("deleted")));
        
        // VERIFY - odds no longer exists
        mockMvc.perform(get("/api/odds/{id}", testOdds.getId())
                .header("Authorization", "Bearer " + adminToken))  // NEW: JWT authentication
                .andExpect(status().isNotFound());
    }

    // ═════════════════════════════════════════════════════════════════════
    // TEST 9: GET /api/odds/sport/{sport} - Filter By Sport - WITH JWT
    // ═════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("GET /api/odds/sport/{sport} - Valid Sport with USER token - Should Return Filtered Results")
    void getOddsBySport_ValidSportWithUserToken_ShouldReturnFilteredResults() throws Exception {
        // WHAT CHANGED?
        // - Added .header("Authorization", "Bearer " + userToken)
        
        // ARRANGE - create basketball odds
        BettingOdds basketballOdds = new BettingOdds();
        basketballOdds.setSport("Basketball");
        basketballOdds.setHomeTeam("Lakers");
        basketballOdds.setAwayTeam("Warriors");
        basketballOdds.setHomeOdds(BigDecimal.valueOf(1.90));
        basketballOdds.setDrawOdds(BigDecimal.valueOf(15.00));
        basketballOdds.setAwayOdds(BigDecimal.valueOf(2.00));
        basketballOdds.setMatchDate(futureDate);
        basketballOdds.setActive(true);
        bettingOddsRepository.save(basketballOdds);
        
        // ACT & ASSERT - get only Football
        mockMvc.perform(get("/api/odds/sport/{sport}", "Football")
                .header("Authorization", "Bearer " + userToken)  // NEW: JWT authentication
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].sport").value("Football"));
    }

    // ═════════════════════════════════════════════════════════════════════
    // TEST 10: GET /api/odds/{id}/margin - Calculate Margin - WITH JWT
    // ═════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("GET /api/odds/{id}/margin - Valid ID with USER token - Should Return Odds with Margin Calculation")
    void getOddsWithMargin_ValidIdWithUserToken_ShouldReturnWithCalculations() throws Exception {
        // WHAT CHANGED?
        // - Added .header("Authorization", "Bearer " + userToken)
        
        // ACT & ASSERT
        mockMvc.perform(get("/api/odds/{id}/margin", testOdds.getId())
                .header("Authorization", "Bearer " + userToken))  // NEW: JWT authentication
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testOdds.getId()))
                .andExpect(jsonPath("$.impliedProbabilityHome").exists())
                .andExpect(jsonPath("$.impliedProbabilityDraw").exists())
                .andExpect(jsonPath("$.impliedProbabilityAway").exists())
                .andExpect(jsonPath("$.bookmakerMargin").exists())
                .andExpect(jsonPath("$.bookmakerMargin").value(greaterThan(4.0)))
                .andExpect(jsonPath("$.bookmakerMargin").value(lessThan(5.0)));
    }

    // ═════════════════════════════════════════════════════════════════════
    // TEST 11: POST /api/odds - SQL Injection Attempt - WITH JWT
    // ═════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("POST /api/odds - SQL Injection Attempt with BOOKMAKER token - Should Return 400 Bad Request")
    void createOdds_SqlInjectionAttemptWithBookmakerToken_ShouldReturn400BadRequest() throws Exception {
        // WHAT CHANGED?
        // - Added .header("Authorization", "Bearer " + bookmakerToken)
        
        // ARRANGE - malicious SQL injection payload
        String jsonRequest = """
            {
                "sport": "Football",
                "homeTeam": "Barcelona'; DROP TABLE betting_odds--",
                "awayTeam": "Real Madrid",
                "homeOdds": 2.10,
                "drawOdds": 3.40,
                "awayOdds": 3.60,
                "matchDate": "%s"
            }
            """.formatted(futureDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        // ACT & ASSERT
        mockMvc.perform(post("/api/odds")
                .header("Authorization", "Bearer " + bookmakerToken)  // NEW: JWT authentication
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Suspicious input")));
    }

    // ═════════════════════════════════════════════════════════════════════
    // TEST 12: GET /api/odds/upcoming - Get Upcoming Matches - WITH JWT
    // ═════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("GET /api/odds/upcoming - Future Matches with USER token - Should Return Only Future Matches")
    void getUpcomingMatches_WithUserToken_ShouldReturnOnlyFutureMatches() throws Exception {
        // WHAT CHANGED?
        // - Added .header("Authorization", "Bearer " + userToken)
        
        // ARRANGE - create past match
        BettingOdds pastOdds = new BettingOdds();
        pastOdds.setSport("Football");
        pastOdds.setHomeTeam("Chelsea");
        pastOdds.setAwayTeam("Arsenal");
        pastOdds.setHomeOdds(BigDecimal.valueOf(2.50));
        pastOdds.setDrawOdds(BigDecimal.valueOf(3.20));
        pastOdds.setAwayOdds(BigDecimal.valueOf(2.80));
        pastOdds.setMatchDate(LocalDateTime.now().minusDays(7)); // PAST
        pastOdds.setActive(true);
        bettingOddsRepository.save(pastOdds);
        
        // ACT & ASSERT - should only return future match
        mockMvc.perform(get("/api/odds/upcoming")
                .header("Authorization", "Bearer " + userToken)  // NEW: JWT authentication
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].homeTeam").value("Barcelona"));
    }

    // ═════════════════════════════════════════════════════════════════════
    // NEW TESTS - ROLE-BASED AUTHORIZATION (403 FORBIDDEN)
    // ═════════════════════════════════════════════════════════════════════

    // ═════════════════════════════════════════════════════════════════════
    // TEST 13: POST /api/odds - USER Role Forbidden (403)
    // ═════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("POST /api/odds - USER role attempting to create - Should Return 403 Forbidden")
    void createOdds_WithUserRole_ShouldReturn403Forbidden() throws Exception {
        // WHAT DOES THIS TEST?
        // - USER role CANNOT create odds (read-only access)
        // - Should return 403 Forbidden (authenticated but insufficient permissions)
        // - Required role: BOOKMAKER or ADMIN
        
        // ARRANGE
        String jsonRequest = """
            {
                "sport": "Basketball",
                "homeTeam": "Lakers",
                "awayTeam": "Warriors",
                "homeOdds": 1.90,
                "drawOdds": 15.00,
                "awayOdds": 2.00,
                "matchDate": "%s"
            }
            """.formatted(futureDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        // ACT & ASSERT
        mockMvc.perform(post("/api/odds")
                .header("Authorization", "Bearer " + userToken)  // USER role (insufficient permissions)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isForbidden())  // 403 Forbidden
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value(containsString("don't have permission")));
    }

    // ═════════════════════════════════════════════════════════════════════
    // TEST 14: PUT /api/odds/{id} - USER Role Forbidden (403)
    // ═════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("PUT /api/odds/{id} - USER role attempting to update - Should Return 403 Forbidden")
    void updateOdds_WithUserRole_ShouldReturn403Forbidden() throws Exception {
        // WHAT DOES THIS TEST?
        // - USER role CANNOT update odds (read-only access)
        // - Should return 403 Forbidden
        // - Required role: BOOKMAKER or ADMIN
        
        // ARRANGE
        String jsonRequest = """
            {
                "sport": "Football",
                "homeTeam": "Barcelona",
                "awayTeam": "Real Madrid",
                "homeOdds": 2.20,
                "drawOdds": 3.50,
                "awayOdds": 3.70,
                "matchDate": "%s",
                "active": true
            }
            """.formatted(futureDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        // ACT & ASSERT
        mockMvc.perform(put("/api/odds/{id}", testOdds.getId())
                .header("Authorization", "Bearer " + userToken)  // USER role (insufficient permissions)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isForbidden())  // 403 Forbidden
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value(containsString("don't have permission")));
    }

    // ═════════════════════════════════════════════════════════════════════
    // TEST 15: PATCH /api/odds/{id}/deactivate - USER Role Forbidden (403)
    // ═════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("PATCH /api/odds/{id}/deactivate - USER role attempting to deactivate - Should Return 403 Forbidden")
    void deactivateOdds_WithUserRole_ShouldReturn403Forbidden() throws Exception {
        // WHAT DOES THIS TEST?
        // - USER role CANNOT deactivate odds (read-only access)
        // - Should return 403 Forbidden
        // - Required role: BOOKMAKER or ADMIN
        
        // ACT & ASSERT
        mockMvc.perform(patch("/api/odds/{id}/deactivate", testOdds.getId())
                .header("Authorization", "Bearer " + userToken))  // USER role (insufficient permissions)
                .andExpect(status().isForbidden())  // 403 Forbidden
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value(containsString("don't have permission")));
    }

    // ═════════════════════════════════════════════════════════════════════
    // TEST 16: DELETE /api/odds/{id} - USER Role Forbidden (403)
    // ═════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("DELETE /api/odds/{id} - USER role attempting to delete - Should Return 403 Forbidden")
    void deleteOdds_WithUserRole_ShouldReturn403Forbidden() throws Exception {
        // WHAT DOES THIS TEST?
        // - USER role CANNOT delete odds (no delete permissions)
        // - Should return 403 Forbidden
        // - Required role: ADMIN only
        
        // ACT & ASSERT
        mockMvc.perform(delete("/api/odds/{id}", testOdds.getId())
                .header("Authorization", "Bearer " + userToken))  // USER role (insufficient permissions)
                .andExpect(status().isForbidden())  // 403 Forbidden
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value(containsString("don't have permission")));
    }

    // ═════════════════════════════════════════════════════════════════════
    // TEST 17: DELETE /api/odds/{id} - BOOKMAKER Role Forbidden (403)
    // ═════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("DELETE /api/odds/{id} - BOOKMAKER role attempting to delete - Should Return 403 Forbidden")
    void deleteOdds_WithBookmakerRole_ShouldReturn403Forbidden() throws Exception {
        // WHAT DOES THIS TEST?
        // - BOOKMAKER role CANNOT delete odds (no delete permissions)
        // - Should return 403 Forbidden
        // - Required role: ADMIN only
        // - BOOKMAKER can create/update but NOT delete
        
        // ACT & ASSERT
        mockMvc.perform(delete("/api/odds/{id}", testOdds.getId())
                .header("Authorization", "Bearer " + bookmakerToken))  // BOOKMAKER role (insufficient permissions)
                .andExpect(status().isForbidden())  // 403 Forbidden
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value(containsString("don't have permission")));
    }

    // ═════════════════════════════════════════════════════════════════════
    // TEST 18: Request Without JWT Token - Should Return 401 Unauthorized
    // ═════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("GET /api/odds - No JWT token - Should Return 401 Unauthorized")
    void getOdds_WithoutToken_ShouldReturn401Unauthorized() throws Exception {
        // WHAT DOES THIS TEST?
        // - Request without JWT token should be rejected
        // - Should return 401 Unauthorized (not 403 Forbidden!)
        // - 401: Authentication required (no/invalid token)
        // - 403: Authenticated but insufficient permissions
        
        // ACT & ASSERT - NO Authorization header
        mockMvc.perform(get("/api/odds"))
                .andExpect(status().isUnauthorized())  // 401 Unauthorized
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value(containsString("Authentication required")));
    }

    // ═════════════════════════════════════════════════════════════════════
    // SUMMARY
    // ═════════════════════════════════════════════════════════════════════
    /*
    UPDATED TESTS (12): Added JWT authentication to all existing tests
    1. POST /api/odds - Create (with BOOKMAKER token)
    2. POST /api/odds - Validation error (with BOOKMAKER token)
    3. GET /api/odds/{id} - Find by ID (with USER token)
    4. GET /api/odds/{id} - Not found (with USER token)
    5. GET /api/odds - Pagination (with USER token)
    6. PUT /api/odds/{id} - Update (with BOOKMAKER token)
    7. PATCH /api/odds/{id}/deactivate - Soft delete (with BOOKMAKER token)
    8. DELETE /api/odds/{id} - Hard delete (with ADMIN token)
    9. GET /api/odds/sport/{sport} - Filter by sport (with USER token)
    10. GET /api/odds/{id}/margin - Calculate margin (with USER token)
    11. POST /api/odds - SQL injection security (with BOOKMAKER token)
    12. GET /api/odds/upcoming - Future matches (with USER token)

    NEW TESTS (6): Role-based authorization (403 Forbidden)
    13. POST /api/odds - USER role forbidden
    14. PUT /api/odds/{id} - USER role forbidden
    15. PATCH /api/odds/{id}/deactivate - USER role forbidden
    16. DELETE /api/odds/{id} - USER role forbidden
    17. DELETE /api/odds/{id} - BOOKMAKER role forbidden
    18. GET /api/odds - No token (401 Unauthorized)

    TOTAL: 18 integration tests
    Coverage: All endpoints + authentication + authorization
    Test type: End-to-end with JWT authentication + role-based access control

    ROLE PERMISSIONS TESTED:
    - USER: Can GET (read-only) âœ…
    - USER: Cannot POST/PUT/PATCH/DELETE (403) âœ…
    - BOOKMAKER: Can GET/POST/PUT/PATCH âœ…
    - BOOKMAKER: Cannot DELETE (403) âœ…
    - ADMIN: Can do everything âœ…
    - No token: 401 Unauthorized âœ…
    */
}