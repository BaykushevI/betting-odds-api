package com.gambling.betting_odds_api.service;

// ═══════════════════════════════════════════════════════════════════════════
// JUNIT 5 IMPORTS - Testing framework
// ═══════════════════════════════════════════════════════════════════════════
// JUnit 5 is the latest version of the most popular Java testing framework.
// It provides annotations and assertions for writing and running tests.

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

// @Test - Marks a method as a test method
// @DisplayName - Provides a custom display name for the test (appears in reports)
// @BeforeEach - Runs before each test method (setup)
// @ExtendWith - Registers extensions (we use MockitoExtension for mocking)

import static org.junit.jupiter.api.Assertions.*;
// Static import for assertions: assertEquals, assertNotNull, assertThrows, etc.
// This allows us to write: assertEquals(expected, actual)
// Instead of: Assertions.assertEquals(expected, actual)

// ═══════════════════════════════════════════════════════════════════════════
// MOCKITO IMPORTS - Mocking framework
// ═══════════════════════════════════════════════════════════════════════════
// Mockito is a mocking framework that allows us to create mock objects.
// Mocking is ESSENTIAL for unit testing because:
// 1. We want to test ONE component in isolation
// 2. We don't want to depend on external systems (database, APIs)
// 3. We want tests to run FAST (no actual database queries)
// 4. We want full control over test data and behavior

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// @Mock - Creates a mock object (fake dependency)
// @InjectMocks - Creates an instance and injects mocks into it
// MockitoExtension - Enables Mockito annotations in JUnit 5

import static org.mockito.Mockito.*;
// Static import for Mockito methods: when(), verify(), any(), etc.
// This allows us to write: when(repository.save(any())).thenReturn(...)
// Instead of: Mockito.when(repository.save(Mockito.any())).thenReturn(...)

// NOTE: eq() and anyLong() are also available from Mockito.*, but importing
// from ArgumentMatchers makes it explicit that we're using argument matchers.
// We use these in verify() calls: eq(1L), anyLong()

// ═══════════════════════════════════════════════════════════════════════════
// PROJECT IMPORTS - Our classes
// ═══════════════════════════════════════════════════════════════════════════
import com.gambling.betting_odds_api.dto.CreateOddsRequest;
import com.gambling.betting_odds_api.dto.OddsResponse;
import com.gambling.betting_odds_api.mapper.OddsMapper;
import com.gambling.betting_odds_api.model.BettingOdds;
import com.gambling.betting_odds_api.repository.BettingOddsRepository;
import com.gambling.betting_odds_api.logging.AuditLogger;
import com.gambling.betting_odds_api.logging.PerformanceLogger;
import com.gambling.betting_odds_api.logging.SecurityLogger;

// ═══════════════════════════════════════════════════════════════════════════
// JAVA IMPORTS
// ═══════════════════════════════════════════════════════════════════════════
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Unit tests for BettingOddsService
 * 
 * WHAT IS A UNIT TEST?
 * A unit test tests a SINGLE component (class/method) in ISOLATION.
 * We use mocks for all dependencies so we only test the Service logic.
 * 
 * UNIT TEST vs INTEGRATION TEST:
 * - Unit Test: Tests ONE class, mocks all dependencies, NO database
 * - Integration Test: Tests multiple classes together, REAL database
 * 
 * WHY UNIT TESTS?
 * 1. Fast execution (milliseconds, no database)
 * 2. Isolated (test only Service logic, not Repository logic)
 * 3. Easy to debug (if test fails, you know exactly where)
 * 4. Documentation (tests show how the class should be used)
 * 
 * NAMING CONVENTION:
 * ClassNameTest (e.g., BettingOddsServiceTest)
 * 
 * TEST METHOD NAMING:
 * methodName_scenario_expectedBehavior
 * Example: createOdds_ValidInput_ReturnsOddsResponse
 */
@ExtendWith(MockitoExtension.class)
// This annotation tells JUnit 5 to use Mockito for this test class.
// It enables @Mock and @InjectMocks annotations.
// Without this, Mockito annotations won't work!

@DisplayName("BettingOddsService Unit Tests")
// Custom display name for the entire test class.
// This appears in test reports and IDE test runners.
// Makes reports more readable than just "BettingOddsServiceTest"

public class BettingOddsServiceTest {
    
    // ═══════════════════════════════════════════════════════════════════════
    // MOCK DEPENDENCIES
    // ═══════════════════════════════════════════════════════════════════════
    // These are FAKE objects that simulate real dependencies.
    // We use @Mock to create them.
    // 
    // WHY MOCK?
    // - We don't want to use a real database (slow, requires setup)
    // - We want full control over what methods return
    // - We want to verify that methods were called correctly
    
    @Mock
    private BettingOddsRepository repository;
    // Mock repository - no real database queries will happen
    // We'll tell it what to return when methods are called
    
    @Mock
    private OddsMapper mapper;
    // Mock mapper - we'll control what it returns when converting DTOs
    
    @Mock
    private AuditLogger auditLogger;
    // Mock audit logger - we'll verify it was called correctly
    
    @Mock
    private PerformanceLogger performanceLogger;
    // Mock performance logger
    
    @Mock
    private SecurityLogger securityLogger;
    // Mock security logger
    
    // ═══════════════════════════════════════════════════════════════════════
    // SYSTEM UNDER TEST (SUT)
    // ═══════════════════════════════════════════════════════════════════════
    // This is the actual object we're testing.
    // @InjectMocks automatically injects all @Mock objects into it.
    
    @InjectMocks
    private BettingOddsService service;
    // This is a REAL BettingOddsService object, but with MOCK dependencies.
    // Mockito will automatically inject: repository, mapper, auditLogger, etc.
    // This is equivalent to:
    // service = new BettingOddsService(repository, mapper, auditLogger, ...);
    
    // ═══════════════════════════════════════════════════════════════════════
    // TEST DATA - Variables used across multiple tests
    // ═══════════════════════════════════════════════════════════════════════
    // We declare these as instance variables so we can use them in multiple tests.
    // They will be initialized in @BeforeEach method.
    
    private CreateOddsRequest request;
    private BettingOdds entity;
    private OddsResponse response;
    
    // ═══════════════════════════════════════════════════════════════════════
    // SETUP METHOD
    // ═══════════════════════════════════════════════════════════════════════
    // @BeforeEach runs BEFORE each test method.
    // Use it to initialize test data that's needed by multiple tests.
    // 
    // WHY @BeforeEach instead of constructor?
    // - JUnit creates a NEW test class instance for EACH test method
    // - This ensures tests don't affect each other (isolation)
    // - @BeforeEach runs for each test, providing fresh data
    
    @BeforeEach
    void setUp() {
        // SETUP TEST DATA
        // This method runs before EACH test, so each test gets fresh data.
        // This prevents tests from affecting each other.
        
        // Create a sample CreateOddsRequest (what client sends)
        request = new CreateOddsRequest(
            "Football",                           // sport
            "Barcelona",                           // homeTeam
            "Real Madrid",                         // awayTeam
            BigDecimal.valueOf(2.10),             // homeOdds
            BigDecimal.valueOf(3.40),             // drawOdds
            BigDecimal.valueOf(3.60),             // awayOdds
            LocalDateTime.now().plusDays(7)       // matchDate (future)
        );
        
        // Create a sample BettingOdds entity (what gets saved to database)
        entity = new BettingOdds();
        entity.setId(1L);
        entity.setSport("Football");
        entity.setHomeTeam("Barcelona");
        entity.setAwayTeam("Real Madrid");
        entity.setHomeOdds(BigDecimal.valueOf(2.10));
        entity.setDrawOdds(BigDecimal.valueOf(3.40));
        entity.setAwayOdds(BigDecimal.valueOf(3.60));
        entity.setMatchDate(LocalDateTime.now().plusDays(7));
        entity.setActive(true);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        
        // Create a sample OddsResponse (what service returns to client)
        response = new OddsResponse(
            1L,                                    // id
            "Football",                           // sport
            "Barcelona",                           // homeTeam
            "Real Madrid",                         // awayTeam
            BigDecimal.valueOf(2.10),             // homeOdds
            BigDecimal.valueOf(3.40),             // drawOdds
            BigDecimal.valueOf(3.60),             // awayOdds
            LocalDateTime.now().plusDays(7),      // matchDate
            true,                                  // active
            LocalDateTime.now(),                   // createdAt
            LocalDateTime.now()                    // updatedAt
        );
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // TEST METHODS - CRUD Operations
    // ═══════════════════════════════════════════════════════════════════════
    // Each @Test method tests ONE specific scenario.
    // 
    // TEST STRUCTURE (AAA Pattern):
    // 1. ARRANGE - Set up test data and configure mocks
    // 2. ACT - Call the method we're testing
    // 3. ASSERT - Verify the results and interactions
    
    @Test
    @DisplayName("createOdds - Valid Input - Should Return OddsResponse")
    // TEST METHOD NAMING CONVENTION:
    // methodName_scenario_expectedBehavior
    // This makes it crystal clear what we're testing and what should happen.
    
    void createOdds_ValidInput_ShouldReturnOddsResponse() {
        // This is our FIRST test! We're testing the "happy path" scenario.
        // Happy path = everything works perfectly, no errors.
        // 
        // WHAT ARE WE TESTING?
        // We're testing that when createOdds() is called with valid input:
        // 1. It converts the request DTO to an entity (using mapper)
        // 2. It saves the entity to the database (using repository)
        // 3. It converts the saved entity back to a response DTO
        // 4. It logs the creation (using auditLogger)
        // 5. It returns the correct OddsResponse
        
        // ═══════════════════════════════════════════════════════════════════
        // ARRANGE - Set up test data and configure mock behavior
        // ═══════════════════════════════════════════════════════════════════
        // In this section, we tell our mocks what to return when called.
        // This is called "stubbing" - we stub out the behavior of dependencies.
        
        // STUB 1: Tell mapper what to return when toEntity() is called
        when(mapper.toEntity(request)).thenReturn(entity);
        // Translation: "When mapper.toEntity() is called with our request,
        //              return our pre-configured entity object"
        // 
        // WHY? Because we're NOT testing the mapper here!
        // We're testing the Service. We assume the mapper works correctly.
        // We just need it to return something so the Service can continue.
        
        // STUB 2: Tell repository what to return when save() is called
        when(repository.save(entity)).thenReturn(entity);
        // Translation: "When repository.save() is called with our entity,
        //              return the same entity (as if it was successfully saved)"
        // 
        // WHY? Because we're NOT testing the repository here!
        // In a real database, save() would generate an ID and timestamps.
        // Here, we just return what was passed in (with ID already set in setUp).
        
        // STUB 3: Tell mapper what to return when toResponse() is called
        when(mapper.toResponse(entity)).thenReturn(response);
        // Translation: "When mapper.toResponse() is called with our entity,
        //              return our pre-configured response object"
        
        // NOTE: We don't need to stub the loggers because they return void.
        // We'll verify they were called later using verify().
        
        // ═══════════════════════════════════════════════════════════════════
        // ACT - Execute the method we're testing
        // ═══════════════════════════════════════════════════════════════════
        // This is the ACTUAL METHOD CALL we're testing.
        // Everything else in the test is setup or verification.
        
        OddsResponse result = service.createOdds(request);
        // Call the createOdds method with our test request.
        // This will:
        // 1. Call mapper.toEntity(request) - returns our stubbed entity
        // 2. Call repository.save(entity) - returns our stubbed entity
        // 3. Call mapper.toResponse(entity) - returns our stubbed response
        // 4. Call auditLogger.logOddsCreated(...)
        // 5. Return the response
        
        // ═══════════════════════════════════════════════════════════════════
        // ASSERT - Verify the results
        // ═══════════════════════════════════════════════════════════════════
        // Now we verify that:
        // 1. The method returned the correct value
        // 2. The mocks were called with correct parameters
        // 3. The mocks were called the correct number of times
        
        // ASSERTION 1: Verify the result is not null
        assertNotNull(result);
        // If result is null, this test FAILS immediately.
        // This is a basic sanity check.
        
        // ASSERTION 2: Verify the result has the correct ID
        assertEquals(1L, result.getId());
        // assertEquals(expected, actual)
        // We expect ID to be 1L (set in our setUp() method)
        
        // ASSERTION 3: Verify the result has correct sport
        assertEquals("Football", result.getSport());
        
        // ASSERTION 4: Verify the result has correct teams
        assertEquals("Barcelona", result.getHomeTeam());
        assertEquals("Real Madrid", result.getAwayTeam());
        
        // ASSERTION 5: Verify the result has correct odds
        assertEquals(BigDecimal.valueOf(2.10), result.getHomeOdds());
        assertEquals(BigDecimal.valueOf(3.40), result.getDrawOdds());
        assertEquals(BigDecimal.valueOf(3.60), result.getAwayOdds());
        
        // ASSERTION 6: Verify the result is active
        assertTrue(result.getActive());
        // assertTrue() checks that the value is true
        
        // ═══════════════════════════════════════════════════════════════════
        // VERIFY - Check that mocks were called correctly
        // ═══════════════════════════════════════════════════════════════════
        // Verify is different from assert:
        // - Assert checks return VALUES
        // - Verify checks METHOD CALLS (interactions)
        // 
        // WHY VERIFY?
        // We want to ensure the Service is calling its dependencies correctly.
        // For example, if the Service never called repository.save(),
        // the odds wouldn't be saved, even if it returned a response!
        
        // VERIFY 1: Check that mapper.toEntity() was called exactly once
        verify(mapper, times(1)).toEntity(request);
        // times(1) means "exactly once"
        // If it was called 0 times or 2 times, test FAILS
        // 
        // Alternative syntax (same meaning):
        // verify(mapper).toEntity(request);  // defaults to times(1)
        
        // VERIFY 2: Check that repository.save() was called exactly once
        verify(repository, times(1)).save(entity);
        // This ensures the Service actually saved the entity to database
        
        // VERIFY 3: Check that mapper.toResponse() was called exactly once
        verify(mapper, times(1)).toResponse(entity);
        // This ensures the Service converted the entity to a response
        
        // VERIFY 4: Check that audit logger was called
        verify(auditLogger, times(1)).logOddsCreated(
            eq(1L),                    // eq() matches exact value
            eq("Football"),
            eq("Barcelona"),
            eq("Real Madrid")
        );
        // This ensures the Service logged the creation for compliance
        
        // VERIFY 5: Check that performance logger was called
        verify(performanceLogger, times(1)).logDatabaseQuery(
            eq("CREATE_ODDS"),
            anyLong()                  // anyLong() matches any long value
        );
        // We use anyLong() because we don't care about the exact duration
        
        // If ANY of these verifications fail, the test FAILS.
        // This ensures the Service is behaving exactly as expected.
        
        // ═══════════════════════════════════════════════════════════════════
        // TEST COMPLETE!
        // ═══════════════════════════════════════════════════════════════════
        // If we reach this point, ALL assertions and verifications passed.
        // The test is considered SUCCESSFUL.
        // 
        // WHAT DID WE LEARN?
        // 1. How to structure a unit test (AAA pattern)
        // 2. How to use @Mock to create fake dependencies
        // 3. How to use when().thenReturn() to stub method behavior
        // 4. How to use assertEquals, assertNotNull, assertTrue
        // 5. How to use verify() to check method calls
        // 6. How to use times(), eq(), anyLong()
        // 
        // NEXT STEPS:
        // In Day 2, we'll write more tests for:
        // - getOddsById()
        // - getAllOdds()
        // - getOddsBySport()
        // - Edge cases (null, empty, invalid)
        // - Exception scenarios (not found, validation errors)
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // MORE TESTS COMING IN DAY 2!
    // ═══════════════════════════════════════════════════════════════════════
    // In the next session, we'll add:
    // - Test for getOddsById() - happy path
    // - Test for getOddsById() - not found (exception)
    // - Test for getAllOdds() - with pagination
    // - Test for getAllOdds() - empty result
    // - Test for updateOdds() - happy path
    // - Test for updateOdds() - not found
    // - Test for deleteOdds() - happy path
    // - Test for deleteOdds() - not found
    // - Test for security validation (SQL injection detection)
    // - Test for margin calculation
}

// ═══════════════════════════════════════════════════════════════════════════
// KEY CONCEPTS SUMMARY
// ═══════════════════════════════════════════════════════════════════════════
//
// UNIT TEST:
// - Tests ONE component in isolation
// - Uses mocks for all dependencies
// - Fast (milliseconds)
// - No database, no network, no external systems
//
// AAA PATTERN:
// - ARRANGE: Set up test data and configure mocks
// - ACT: Call the method being tested
// - ASSERT: Verify results and interactions
//
// MOCKITO:
// - @Mock: Creates a fake object
// - @InjectMocks: Creates real object with mocked dependencies
// - when().thenReturn(): Tell mock what to return
// - verify(): Check that mock was called correctly
//
// JUNIT 5:
// - @Test: Marks a test method
// - @BeforeEach: Runs before each test
// - @DisplayName: Custom test name
// - assertEquals(): Check values are equal
// - assertNotNull(): Check value is not null
// - assertTrue(): Check value is true
//
// NAMING CONVENTIONS:
// - Test class: ClassNameTest
// - Test method: methodName_scenario_expectedBehavior
//
// WHY WRITE TESTS?
// 1. Catch bugs early (before production)
// 2. Documentation (tests show how code should work)
// 3. Refactoring safety (tests ensure you didn't break anything)
// 4. Design feedback (hard to test = bad design)
// 5. Confidence (deploy knowing your code works)
//
// ═══════════════════════════════════════════════════════════════════════════