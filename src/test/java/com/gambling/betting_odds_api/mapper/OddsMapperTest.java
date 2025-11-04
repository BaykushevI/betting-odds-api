package com.gambling.betting_odds_api.mapper;

// JUnit 5 Testing Framework
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

// JUnit 5 Assertions
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

// Your project classes
import com.gambling.betting_odds_api.dto.CreateOddsRequest;
import com.gambling.betting_odds_api.dto.UpdateOddsRequest;
import com.gambling.betting_odds_api.dto.OddsResponse;
import com.gambling.betting_odds_api.model.BettingOdds;

// Java standard library
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Unit Tests for OddsMapper
 * 
 * WHAT IS MAPPER?
 * Mapper converts between DTOs and Entities.
 * - DTO (Data Transfer Object) = what API receives/sends (JSON)
 * - Entity = what database stores
 * 
 * Example flow:
 * Client sends JSON → CreateOddsRequest (DTO) → Mapper.toEntity() → BettingOdds (Entity) → Save to DB
 * 
 * WHY TEST MAPPER?
 * - Ensure all fields are copied correctly
 * - Verify default values are set (active = true)
 * - Check calculations are accurate (margin)
 * - Prevent data loss during conversion
 * 
 * NO MOCKING NEEDED:
 * Mapper has no dependencies - it's a pure function.
 * We test the real Mapper directly.
 */
@DisplayName("OddsMapper Unit Tests")
public class OddsMapperTest {
    
    // Real mapper instance (no mocking)
    private OddsMapper mapper;
    
    // Test data
    private CreateOddsRequest createRequest;
    private UpdateOddsRequest updateRequest;
    private BettingOdds entity;
    private LocalDateTime testDate;
    
    @BeforeEach
    void setUp() {
        // Create REAL mapper instance
        mapper = new OddsMapper();
        
        testDate = LocalDateTime.now().plusDays(7);
        
        // Create test CreateOddsRequest
        createRequest = new CreateOddsRequest(
            "Football",
            "Barcelona",
            "Real Madrid",
            BigDecimal.valueOf(2.10),
            BigDecimal.valueOf(3.40),
            BigDecimal.valueOf(3.60),
            testDate
        );
        
        // Create test UpdateOddsRequest
        updateRequest = new UpdateOddsRequest(
            "Football",
            "Barcelona",
            "Real Madrid",
            BigDecimal.valueOf(2.20),
            BigDecimal.valueOf(3.50),
            BigDecimal.valueOf(3.70),
            testDate,
            true
        );
        
        // Create test Entity
        entity = new BettingOdds();
        entity.setId(1L);
        entity.setSport("Football");
        entity.setHomeTeam("Barcelona");
        entity.setAwayTeam("Real Madrid");
        entity.setHomeOdds(BigDecimal.valueOf(2.10));
        entity.setDrawOdds(BigDecimal.valueOf(3.40));
        entity.setAwayOdds(BigDecimal.valueOf(3.60));
        entity.setMatchDate(testDate);
        entity.setActive(true);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
    }
    
    @Test
    @DisplayName("toEntity - Valid Request - Should Map All Fields")
    void toEntity_ValidRequest_ShouldMapAllFields() {
        // WHAT DOES THIS TEST DO?
        // It checks that mapper.toEntity() correctly converts CreateOddsRequest to BettingOdds.
        // 
        // STEPS:
        // 1. Call mapper.toEntity(createRequest)
        // 2. Check that result is not null
        // 3. Check that all fields were copied from request to entity
        // 4. Check that active is set to TRUE by default
        
        // ACT - call the method we're testing
        BettingOdds result = mapper.toEntity(createRequest);
        
        // ASSERT - check the results
        
        // Check result is not null
        assertNotNull(result);
        // If result is null, test fails with message: "expected: not null"
        
        // Check sport was copied
        assertEquals("Football", result.getSport());
        // If result.getSport() is not "Football", test fails
        // Error: "expected: Football but was: [whatever result.getSport() returned]"
        
        // Check home team was copied
        assertEquals("Barcelona", result.getHomeTeam());
        
        // Check away team was copied
        assertEquals("Real Madrid", result.getAwayTeam());
        
        // Check home odds was copied (BigDecimal comparison)
        assertEquals(0, BigDecimal.valueOf(2.10).compareTo(result.getHomeOdds()));
        // Why compareTo? Because BigDecimal.equals() checks scale too (2.10 != 2.1)
        // compareTo returns 0 if equal, -1 if less, +1 if greater
        
        // Check draw odds
        assertEquals(0, BigDecimal.valueOf(3.40).compareTo(result.getDrawOdds()));
        
        // Check away odds
        assertEquals(0, BigDecimal.valueOf(3.60).compareTo(result.getAwayOdds()));
        
        // Check match date was copied
        assertEquals(testDate, result.getMatchDate());
        
        // Check active is TRUE by default
        assertTrue(result.getActive());
        // If result.getActive() is false or null, test fails
        
        // Check ID is null (will be set by database)
        assertNull(result.getId());
        
        // Check timestamps are null (will be set by JPA)
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
    }
    
    @Test
    @DisplayName("toResponse - Valid Entity - Should Map All Fields")
    void toResponse_ValidEntity_ShouldMapAllFields() {
        // WHAT DOES THIS TEST DO?
        // It checks that mapper.toResponse() correctly converts BettingOdds to OddsResponse.
        
        // ACT
        OddsResponse result = mapper.toResponse(entity);
        
        // ASSERT
        assertNotNull(result);
        
        // Check ID was copied
        assertEquals(1L, result.getId());
        
        // Check all fields
        assertEquals("Football", result.getSport());
        assertEquals("Barcelona", result.getHomeTeam());
        assertEquals("Real Madrid", result.getAwayTeam());
        
        // Check odds
        assertEquals(0, BigDecimal.valueOf(2.10).compareTo(result.getHomeOdds()));
        assertEquals(0, BigDecimal.valueOf(3.40).compareTo(result.getDrawOdds()));
        assertEquals(0, BigDecimal.valueOf(3.60).compareTo(result.getAwayOdds()));
        
        // Check timestamps were copied
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        
        // Check calculated fields are null (not in basic response)
        assertNull(result.getImpliedProbabilityHome());
        assertNull(result.getBookmakerMargin());
    }
    
    @Test
    @DisplayName("toResponseWithMargin - Valid Entity - Should Calculate Margin")
    void toResponseWithMargin_ValidEntity_ShouldCalculateMargin() {
        // WHAT DOES THIS TEST DO?
        // It checks that toResponseWithMargin() includes calculated fields.
        // 
        // CALCULATION:
        // homeOdds = 2.10 → probability = 1/2.10 = 0.476 (47.6%)
        // drawOdds = 3.40 → probability = 1/3.40 = 0.294 (29.4%)
        // awayOdds = 3.60 → probability = 1/3.60 = 0.278 (27.8%)
        // Total = 104.8%
        // Margin = 104.8% - 100% = 4.8%
        
        // ACT
        OddsResponse result = mapper.toResponseWithMargin(entity);
        
        // ASSERT
        assertNotNull(result);
        
        // Check calculated fields exist
        assertNotNull(result.getImpliedProbabilityHome());
        assertNotNull(result.getImpliedProbabilityDraw());
        assertNotNull(result.getImpliedProbabilityAway());
        assertNotNull(result.getBookmakerMargin());
        
        // Check margin is in reasonable range (4-5%)
        assertTrue(result.getBookmakerMargin() > 4.0);
        assertTrue(result.getBookmakerMargin() < 5.0);
    }
    
    @Test
    @DisplayName("updateEntityFromDto - Valid Request - Should Update Fields")
    void updateEntityFromDto_ValidRequest_ShouldUpdateFields() {
        // WHAT DOES THIS TEST DO?
        // It checks that updateEntityFromDto() updates entity fields from DTO.
        // Important: ID and createdAt should NEVER change!
        
        // ARRANGE - remember original values
        Long originalId = entity.getId();
        LocalDateTime originalCreatedAt = entity.getCreatedAt();
        
        // ACT - update entity
        mapper.updateEntityFromDto(entity, updateRequest);
        
        // ASSERT - check fields were updated
        assertEquals("Football", entity.getSport());
        assertEquals("Barcelona", entity.getHomeTeam());
        assertEquals("Real Madrid", entity.getAwayTeam());
        
        // Check odds were updated to new values
        assertEquals(0, BigDecimal.valueOf(2.20).compareTo(entity.getHomeOdds()));
        assertEquals(0, BigDecimal.valueOf(3.50).compareTo(entity.getDrawOdds()));
        assertEquals(0, BigDecimal.valueOf(3.70).compareTo(entity.getAwayOdds()));
        
        // IMPORTANT: Check ID never changed
        assertEquals(originalId, entity.getId());
        
        // IMPORTANT: Check createdAt never changed
        assertEquals(originalCreatedAt, entity.getCreatedAt());
    }
    
    // SUMMARY:
    // We tested 4 main mapper methods:
    // 1. toEntity() - DTO to Entity
    // 2. toResponse() - Entity to DTO
    // 3. toResponseWithMargin() - Entity to DTO with calculations
    // 4. updateEntityFromDto() - Update entity from DTO
    //
    // Total: 4 essential tests
    // All without mocking - mapper is a pure function
}