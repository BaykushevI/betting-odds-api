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
import com.gambling.betting_odds_api.model.BettingOdds;

// Java standard library
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
Integration Tests for BettingOddsController
WHAT IS @SpringBootTest?
Loads FULL Spring application context (all beans)
Uses real database (H2 in-memory for tests)
Real HTTP requests via MockMvc
Slower than @WebMvcTest but tests EVERYTHING
WHAT IS MockMvc?
Simulates HTTP requests without starting real server
Tests REST endpoints end-to-end
Verifies status codes, headers, JSON responses
WHY @Transactional?
Each test rolls back automatically
Database stays clean between tests
No need for manual cleanup
TEST STRUCTURE:
Setup test data in @BeforeEach
Make HTTP request via MockMvc
Verify response (status, JSON, headers)
*/
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("BettingOddsController Integration Tests")
public class BettingOddsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BettingOddsRepository repository;

    private BettingOdds testOdds;
    private LocalDateTime futureDate;
    @BeforeEach
    void setUp() {
    // Clean database
    repository.deleteAll();
    // Create test data
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

    testOdds = repository.save(testOdds);
}
// ═════════════════════════════════════════════════════════════════════
// TEST 1: POST /api/odds - Create New Odds (Happy Path)
// ═════════════════════════════════════════════════════════════════════
@Test
@DisplayName("POST /api/odds - Valid Request - Should Return 201 Created")
void createOdds_ValidRequest_ShouldReturn201Created() throws Exception {
// WHAT DOES THIS TEST?
// - POST request creates new odds
// - Returns 201 CREATED status
// - Returns created odds in JSON
// - ID is auto-generated
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
// TEST 2: POST /api/odds - Invalid Request (Missing Fields)
// ═════════════════════════════════════════════════════════════════════
@Test
@DisplayName("POST /api/odds - Missing Required Fields - Should Return 400 Bad Request")
void createOdds_MissingFields_ShouldReturn400BadRequest() throws Exception {
// WHAT DOES THIS TEST?
// - Bean Validation catches missing fields
// - Returns 400 BAD REQUEST
// - Returns validation error messages
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
         .contentType(MediaType.APPLICATION_JSON)
         .content(jsonRequest))
         .andExpect(status().isBadRequest())
         .andExpect(jsonPath("$.message").value(containsString("Invalid input data")));
}
// ═════════════════════════════════════════════════════════════════════
// TEST 3: GET /api/odds/{id} - Find By ID (Happy Path)
// ═════════════════════════════════════════════════════════════════════
@Test
@DisplayName("GET /api/odds/{id} - Existing ID - Should Return 200 OK")
void getOddsById_ExistingId_ShouldReturn200OK() throws Exception {
// WHAT DOES THIS TEST?
// - GET request retrieves odds by ID
// - Returns 200 OK
// - Returns correct odds data
 // ACT & ASSERT
 mockMvc.perform(get("/api/odds/{id}", testOdds.getId()))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.id").value(testOdds.getId()))
         .andExpect(jsonPath("$.sport").value("Football"))
         .andExpect(jsonPath("$.homeTeam").value("Barcelona"))
         .andExpect(jsonPath("$.awayTeam").value("Real Madrid"))
         .andExpect(jsonPath("$.homeOdds").value(2.10))
         .andExpect(jsonPath("$.active").value(true));
}
// ═════════════════════════════════════════════════════════════════════
// TEST 4: GET /api/odds/{id} - Non-Existing ID
// ═════════════════════════════════════════════════════════════════════
@Test
@DisplayName("GET /api/odds/{id} - Non-Existing ID - Should Return 404 Not Found")
void getOddsById_NonExistingId_ShouldReturn404NotFound() throws Exception {
// WHAT DOES THIS TEST?
// - GET request with invalid ID
// - Returns 404 NOT FOUND
// - Returns error message
 // ARRANGE - use non-existing ID
 Long nonExistingId = 99999L;
 
 // ACT & ASSERT
 mockMvc.perform(get("/api/odds/{id}", nonExistingId))
         .andExpect(status().isNotFound())
         .andExpect(jsonPath("$.message").value(containsString("not found")));
}
// ═════════════════════════════════════════════════════════════════════
// TEST 5: GET /api/odds - Get All Odds with Pagination
// ═════════════════════════════════════════════════════════════════════
@Test
@DisplayName("GET /api/odds - With Pagination - Should Return Page Response")
void getAllOdds_WithPagination_ShouldReturnPageResponse() throws Exception {
// WHAT DOES THIS TEST?
// - GET request with pagination params
// - Returns 200 OK
// - Returns PageResponse structure
// - Includes content, pagination metadata
 // ARRANGE - create more test data
 BettingOdds odds2 = new BettingOdds();
 odds2.setSport("Basketball");
 odds2.setHomeTeam("Lakers");
 odds2.setAwayTeam("Warriors");
 odds2.setHomeOdds(BigDecimal.valueOf(1.90));
 odds2.setDrawOdds(BigDecimal.valueOf(15.00));
 odds2.setAwayOdds(BigDecimal.valueOf(2.00));
 odds2.setMatchDate(futureDate);
 odds2.setActive(true);
 repository.save(odds2);
 
 // ACT & ASSERT
 mockMvc.perform(get("/api/odds")
         .param("page", "0")
         .param("size", "10"))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.content").isArray())
         .andExpect(jsonPath("$.content", hasSize(2)))
         .andExpect(jsonPath("$.totalElements").value(2))
         .andExpect(jsonPath("$.totalPages").value(1))
         .andExpect(jsonPath("$.pageSize").value(10))
         .andExpect(jsonPath("$.pageNumber").value(0));
}
// ═════════════════════════════════════════════════════════════════════
// TEST 6: PUT /api/odds/{id} - Update Odds (Happy Path)
// ═════════════════════════════════════════════════════════════════════
@Test
@DisplayName("PUT /api/odds/{id} - Valid Request - Should Return 200 OK")
void updateOdds_ValidRequest_ShouldReturn200OK() throws Exception {
// WHAT DOES THIS TEST?
// - PUT request updates existing odds
// - Returns 200 OK
// - Returns updated odds
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
         .contentType(MediaType.APPLICATION_JSON)
         .content(jsonRequest))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.id").value(testOdds.getId()))
         .andExpect(jsonPath("$.homeOdds").value(2.20))
         .andExpect(jsonPath("$.drawOdds").value(3.50))
         .andExpect(jsonPath("$.awayOdds").value(3.70));
}
// ═════════════════════════════════════════════════════════════════════
// TEST 7: PATCH /api/odds/{id}/deactivate - Deactivate Odds
// ═════════════════════════════════════════════════════════════════════
@Test
@DisplayName("PATCH /api/odds/{id}/deactivate - Should Return 200 OK")
void deactivateOdds_ValidId_ShouldReturn200OK() throws Exception {
// WHAT DOES THIS TEST?
// - PATCH request deactivates odds (soft delete)
// - Returns 200 OK
// - Returns success message
 // ACT & ASSERT
 mockMvc.perform(patch("/api/odds/{id}/deactivate", testOdds.getId()))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.message").value(containsString("deactivated")));
}
// ═════════════════════════════════════════════════════════════════════
// TEST 8: DELETE /api/odds/{id} - Delete Odds (Hard Delete)
// ═════════════════════════════════════════════════════════════════════
@Test
@DisplayName("DELETE /api/odds/{id} - Should Return 200 OK")
void deleteOdds_ValidId_ShouldReturn200OK() throws Exception {
// WHAT DOES THIS TEST?
// - DELETE request permanently deletes odds
// - Returns 200 OK
// - Returns success message
 // ACT & ASSERT
 mockMvc.perform(delete("/api/odds/{id}", testOdds.getId()))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.message").value(containsString("deleted")));
 
 // VERIFY - odds no longer exists
 mockMvc.perform(get("/api/odds/{id}", testOdds.getId()))
         .andExpect(status().isNotFound());
}
// ═════════════════════════════════════════════════════════════════════
// TEST 9: GET /api/odds/sport/{sport} - Filter By Sport
// ═════════════════════════════════════════════════════════════════════
@Test
@DisplayName("GET /api/odds/sport/{sport} - Should Return Filtered Results")
void getOddsBySport_ValidSport_ShouldReturnFilteredResults() throws Exception {
// WHAT DOES THIS TEST?
// - GET request filters by sport
// - Returns only matching odds
// - Returns 200 OK
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
 repository.save(basketballOdds);
 
 // ACT & ASSERT - get only Football
 mockMvc.perform(get("/api/odds/sport/{sport}", "Football")
         .param("page", "0")
         .param("size", "10"))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.content").isArray())
         .andExpect(jsonPath("$.content", hasSize(1)))
         .andExpect(jsonPath("$.content[0].sport").value("Football"));
}
// ═════════════════════════════════════════════════════════════════════
// TEST 10: GET /api/odds/{id}/margin - Calculate Margin
// ═════════════════════════════════════════════════════════════════════
@Test
@DisplayName("GET /api/odds/{id}/margin - Should Return Odds with Margin Calculation")
void getOddsWithMargin_ValidId_ShouldReturnWithCalculations() throws Exception {
// WHAT DOES THIS TEST?
// - GET request returns odds with margin calculation
// - Returns 200 OK
// - Includes impliedProbability and bookmakerMargin fields
 // ACT & ASSERT
 mockMvc.perform(get("/api/odds/{id}/margin", testOdds.getId()))
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
// TEST 11: POST /api/odds - SQL Injection Attempt
// ═════════════════════════════════════════════════════════════════════
@Test
@DisplayName("POST /api/odds - SQL Injection Attempt - Should Return 400 Bad Request")
void createOdds_SqlInjectionAttempt_ShouldReturn400BadRequest() throws Exception {
// WHAT DOES THIS TEST?
// - Security validation blocks SQL injection
// - Returns 400 BAD REQUEST
// - Returns security error message
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
         .contentType(MediaType.APPLICATION_JSON)
         .content(jsonRequest))
         .andExpect(status().isBadRequest())
         .andExpect(jsonPath("$.message").value(containsString("Suspicious input")));
}
// ═════════════════════════════════════════════════════════════════════
// TEST 12: GET /api/odds/upcoming - Get Upcoming Matches
// ═════════════════════════════════════════════════════════════════════
@Test
@DisplayName("GET /api/odds/upcoming - Should Return Only Future Matches")
void getUpcomingMatches_ShouldReturnOnlyFutureMatches() throws Exception {
// WHAT DOES THIS TEST?
// - GET request filters by future date
// - Returns only upcoming matches
// - Returns 200 OK
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
 repository.save(pastOdds);
 
 // ACT & ASSERT - should only return future match
 mockMvc.perform(get("/api/odds/upcoming")
         .param("page", "0")
         .param("size", "10"))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.content").isArray())
         .andExpect(jsonPath("$.content", hasSize(1)))
         .andExpect(jsonPath("$.content[0].homeTeam").value("Barcelona"));
}
// ═════════════════════════════════════════════════════════════════════
// SUMMARY
// ═════════════════════════════════════════════════════════════════════
// We tested 12 REST endpoints:
// 1. POST /api/odds - Create (happy path)
// 2. POST /api/odds - Validation error
// 3. GET /api/odds/{id} - Find by ID (happy path)
// 4. GET /api/odds/{id} - Not found error
// 5. GET /api/odds - Pagination
// 6. PUT /api/odds/{id} - Update
// 7. PATCH /api/odds/{id}/deactivate - Soft delete
// 8. DELETE /api/odds/{id} - Hard delete
// 9. GET /api/odds/sport/{sport} - Filter by sport
// 10. GET /api/odds/{id}/margin - Calculate margin
// 11. POST /api/odds - SQL injection security
// 12. GET /api/odds/upcoming - Future matches filter
//
// Total: 12 integration tests
// Coverage: All main REST endpoints
// Test type: End-to-end with real HTTP + database
}