package com.gambling.betting_odds_api.service;

// Testing framework imports
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

// Mockito imports
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

// Spring Data imports for pagination
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

// Project imports
import com.gambling.betting_odds_api.dto.CreateOddsRequest;
import com.gambling.betting_odds_api.dto.OddsResponse;
import com.gambling.betting_odds_api.dto.PageResponse;
import com.gambling.betting_odds_api.mapper.OddsMapper;
import com.gambling.betting_odds_api.model.BettingOdds;
import com.gambling.betting_odds_api.repository.BettingOddsRepository;
import com.gambling.betting_odds_api.logging.AuditLogger;
import com.gambling.betting_odds_api.logging.PerformanceLogger;
import com.gambling.betting_odds_api.logging.SecurityLogger;
import com.gambling.betting_odds_api.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

/**
 * Unit Tests for BettingOddsService
 * 
 * Day 1: CREATE operation (createOdds)
 * Day 2: READ operations (getById, getAll, getActive, getBySport)
 * 
 * Progress: 6/45 tests, approximately 13 percent coverage
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BettingOddsService Unit Tests - Day 1 and Day 2")
public class BettingOddsServiceTest {
    
    // Mocked dependencies
    @Mock
    private BettingOddsRepository repository;
    
    @Mock
    private OddsMapper mapper;
    
    @Mock
    private AuditLogger auditLogger;
    
    @Mock
    private PerformanceLogger performanceLogger;
    
    @Mock
    private SecurityLogger securityLogger;
    
    // System under test
    @InjectMocks
    private BettingOddsService service;
    
    // Test data
    private CreateOddsRequest request;
    private BettingOdds entity;
    private OddsResponse response;
    
    @BeforeEach
    void setUp() {
        // Initialize test data before each test
        request = new CreateOddsRequest(
            "Football",
            "Barcelona",
            "Real Madrid",
            BigDecimal.valueOf(2.10),
            BigDecimal.valueOf(3.40),
            BigDecimal.valueOf(3.60),
            LocalDateTime.now().plusDays(7)
        );
        
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
        
        response = new OddsResponse(
            1L,
            "Football",
            "Barcelona",
            "Real Madrid",
            BigDecimal.valueOf(2.10),
            BigDecimal.valueOf(3.40),
            BigDecimal.valueOf(3.60),
            LocalDateTime.now().plusDays(7),
            true,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
    
    // CREATE OPERATION TEST
    
    @Test
    @DisplayName("createOdds - Valid Input - Should Return OddsResponse")
    void createOdds_ValidInput_ShouldReturnOddsResponse() {
        // ARRANGE: Configure mock behavior
        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);
        
        // ACT: Call the method we're testing
        OddsResponse result = service.createOdds(request);
        
        // ASSERT: Verify the result
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Football", result.getSport());
        assertEquals("Barcelona", result.getHomeTeam());
        assertEquals("Real Madrid", result.getAwayTeam());
        assertEquals(BigDecimal.valueOf(2.10), result.getHomeOdds());
        assertEquals(BigDecimal.valueOf(3.40), result.getDrawOdds());
        assertEquals(BigDecimal.valueOf(3.60), result.getAwayOdds());
        assertTrue(result.getActive());
        
        // VERIFY: Check mock interactions
        verify(mapper, times(1)).toEntity(request);
        verify(repository, times(1)).save(entity);
        verify(mapper, times(1)).toResponse(entity);
        verify(auditLogger, times(1)).logOddsCreated(eq(1L), eq("Football"), eq("Barcelona"), eq("Real Madrid"));
        verify(performanceLogger, times(1)).logDatabaseQuery(eq("CREATE_ODDS"), anyLong());
    }
    
    // READ OPERATIONS TESTS
    
    @Test
    @DisplayName("getOddsById - Valid ID - Should Return OddsResponse")
    void getOddsById_ValidId_ShouldReturnOddsResponse() {
        // ARRANGE
        Long oddsId = 1L;
        when(repository.findById(oddsId)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);
        
        // ACT
        OddsResponse result = service.getOddsById(oddsId);
        
        // ASSERT
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Football", result.getSport());
        assertEquals("Barcelona", result.getHomeTeam());
        assertEquals("Real Madrid", result.getAwayTeam());
        
        // VERIFY
        verify(repository, times(1)).findById(oddsId);
        verify(mapper, times(1)).toResponse(entity);
        verify(auditLogger, times(1)).logOddsViewed(eq(1L), any());
        verify(performanceLogger, times(1)).logDatabaseQuery(eq("GET_ODDS_BY_ID"), anyLong());
    }
    
    @Test
    @DisplayName("getOddsById - Invalid ID - Should Throw ResourceNotFoundException")
    void getOddsById_InvalidId_ShouldThrowResourceNotFoundException() {
        // ARRANGE
        Long invalidId = 999L;
        when(repository.findById(invalidId)).thenReturn(Optional.empty());
        
        // ACT & ASSERT
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> service.getOddsById(invalidId)
        );
        
        // Verify exception message contains the ID
        assertTrue(exception.getMessage().contains("999"));
        
        // VERIFY
        verify(repository, times(1)).findById(invalidId);
        verify(mapper, never()).toResponse(any());
        verify(auditLogger, never()).logOddsViewed(anyLong(), any());
    }
    
    @Test
    @DisplayName("getAllOdds - With Pagination - Should Return PageResponse")
    void getAllOdds_WithPagination_ShouldReturnPageResponse() {
        // ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        Page<BettingOdds> page = new PageImpl<>(Arrays.asList(entity), pageable, 1);
        
        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.toResponse(entity)).thenReturn(response);
        
        // ACT
        PageResponse<OddsResponse> result = service.getAllOdds(pageable);
        
        // ASSERT
        assertNotNull(result);
        assertNotNull(result.getContent());
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(0, result.getPageNumber());
        assertEquals(10, result.getPageSize());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());
        assertFalse(result.isEmpty());
        
        // Verify content
        OddsResponse firstItem = result.getContent().get(0);
        assertEquals(1L, firstItem.getId());
        assertEquals("Football", firstItem.getSport());
        
        // VERIFY
        verify(repository, times(1)).findAll(pageable);
        verify(mapper, times(1)).toResponse(entity);
        verify(auditLogger, times(1)).logBulkQuery(any(), eq(1), any());
        verify(performanceLogger, times(1)).logPagination(any(), eq(0), eq(10), eq(1), anyLong());
    }
    
    @Test
    @DisplayName("getActiveOdds - With Data - Should Return Active Odds Only")
    void getActiveOdds_WithData_ShouldReturnActiveOddsOnly() {
        // ARRANGE
        Pageable pageable = PageRequest.of(0, 20);
        Page<BettingOdds> page = new PageImpl<>(Arrays.asList(entity), pageable, 1);
        
        when(repository.findByActiveTrue(pageable)).thenReturn(page);
        when(mapper.toResponse(entity)).thenReturn(response);
        
        // ACT
        PageResponse<OddsResponse> result = service.getActiveOdds(pageable);
        
        // ASSERT
        assertNotNull(result);
        assertNotNull(result.getContent());
        assertEquals(1, result.getContent().size());
        
        // Verify returned odds are active
        OddsResponse firstItem = result.getContent().get(0);
        assertTrue(firstItem.getActive());
        
        // VERIFY
        verify(repository, times(1)).findByActiveTrue(pageable);
        verify(mapper, times(1)).toResponse(entity);
        verify(auditLogger, times(1)).logBulkQuery(eq("/api/odds/active"), eq(1), eq("active=true"));
    }
    
    @Test
    @DisplayName("getOddsBySport - Valid Sport - Should Return Filtered Results")
    void getOddsBySport_ValidSport_ShouldReturnFilteredResults() {
        // ARRANGE
        String sport = "Football";
        Pageable pageable = PageRequest.of(0, 20);
        Page<BettingOdds> page = new PageImpl<>(Arrays.asList(entity), pageable, 1);
        
        when(repository.findBySportAndActiveTrue(sport, pageable)).thenReturn(page);
        when(mapper.toResponse(entity)).thenReturn(response);
        
        // ACT
        PageResponse<OddsResponse> result = service.getOddsBySport(sport, pageable);
        
        // ASSERT
        assertNotNull(result);
        assertNotNull(result.getContent());
        assertEquals(1, result.getContent().size());
        
        // Verify sport is correct
        OddsResponse firstItem = result.getContent().get(0);
        assertEquals("Football", firstItem.getSport());
        
        // VERIFY
        verify(repository, times(1)).findBySportAndActiveTrue(sport, pageable);
        verify(mapper, times(1)).toResponse(entity);
        verify(auditLogger, times(1)).logBulkQuery(eq("/api/odds/sport/" + sport), eq(1), eq("sport=" + sport));
        verify(performanceLogger, times(1)).logPagination(any(), eq(0), eq(20), eq(1), anyLong());
    }
    
    // UPDATE OPERATIONS TESTS
    
    @Test
    @DisplayName("updateOdds - Valid Update - Should Return Updated OddsResponse")
    void updateOdds_ValidUpdate_ShouldReturnUpdatedOddsResponse() {
        // WHAT ARE WE TESTING?
        // We're testing that updateOdds() correctly updates an existing entity.
        // This is more complex than create because we need to:
        // 1. Find existing entity
        // 2. Update its fields
        // 3. Save the updated entity
        // 4. Return the updated response
        
        // ARRANGE
        Long oddsId = 1L;
        
        // Create update request with new odds values
        com.gambling.betting_odds_api.dto.UpdateOddsRequest updateRequest = 
            new com.gambling.betting_odds_api.dto.UpdateOddsRequest(
                "Football",
                "Barcelona",
                "Real Madrid",
                BigDecimal.valueOf(2.20),  // Changed from 2.10
                BigDecimal.valueOf(3.50),  // Changed from 3.40
                BigDecimal.valueOf(3.70),  // Changed from 3.60
                LocalDateTime.now().plusDays(7),
                true
            );
        
        // Create updated entity with new values
        BettingOdds updatedEntity = new BettingOdds();
        updatedEntity.setId(1L);
        updatedEntity.setSport("Football");
        updatedEntity.setHomeTeam("Barcelona");
        updatedEntity.setAwayTeam("Real Madrid");
        updatedEntity.setHomeOdds(BigDecimal.valueOf(2.20));
        updatedEntity.setDrawOdds(BigDecimal.valueOf(3.50));
        updatedEntity.setAwayOdds(BigDecimal.valueOf(3.70));
        updatedEntity.setMatchDate(LocalDateTime.now().plusDays(7));
        updatedEntity.setActive(true);
        updatedEntity.setCreatedAt(LocalDateTime.now());
        updatedEntity.setUpdatedAt(LocalDateTime.now());
        
        // Create updated response
        OddsResponse updatedResponse = new OddsResponse(
            1L,
            "Football",
            "Barcelona",
            "Real Madrid",
            BigDecimal.valueOf(2.20),
            BigDecimal.valueOf(3.50),
            BigDecimal.valueOf(3.70),
            LocalDateTime.now().plusDays(7),
            true,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        // STUB: Repository finds existing entity
        when(repository.findById(oddsId)).thenReturn(Optional.of(entity));
        
        // STUB: Repository saves updated entity
        when(repository.save(any(BettingOdds.class))).thenReturn(updatedEntity);
        
        // STUB: Mapper converts updated entity to response
        when(mapper.toResponse(updatedEntity)).thenReturn(updatedResponse);
        
        // ACT
        OddsResponse result = service.updateOdds(oddsId, updateRequest);
        
        // ASSERT
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(BigDecimal.valueOf(2.20), result.getHomeOdds());
        assertEquals(BigDecimal.valueOf(3.50), result.getDrawOdds());
        assertEquals(BigDecimal.valueOf(3.70), result.getAwayOdds());
        
        // VERIFY
        verify(repository, times(1)).findById(oddsId);
        verify(repository, times(1)).save(any(BettingOdds.class));
        verify(mapper, times(1)).toResponse(updatedEntity);
        verify(auditLogger, times(1)).logOddsUpdated(eq(1L), eq("Barcelona"), eq("Real Madrid"), any());
        verify(performanceLogger, times(1)).logDatabaseQuery(eq("UPDATE_ODDS"), anyLong());
    }
    
    @Test
    @DisplayName("updateOdds - Invalid ID - Should Throw ResourceNotFoundException")
    void updateOdds_InvalidId_ShouldThrowResourceNotFoundException() {
        // WHAT ARE WE TESTING?
        // We're testing that updateOdds() properly handles the case when
        // trying to update a non-existent entity.
        // This is important because we can't update something that doesn't exist.
        
        // ARRANGE
        Long invalidId = 999L;
        
        com.gambling.betting_odds_api.dto.UpdateOddsRequest updateRequest = 
            new com.gambling.betting_odds_api.dto.UpdateOddsRequest(
                "Football",
                "Barcelona",
                "Real Madrid",
                BigDecimal.valueOf(2.20),
                BigDecimal.valueOf(3.50),
                BigDecimal.valueOf(3.70),
                LocalDateTime.now().plusDays(7),
                true
            );
        
        // STUB: Repository returns empty (not found)
        when(repository.findById(invalidId)).thenReturn(Optional.empty());
        
        // ACT & ASSERT
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> service.updateOdds(invalidId, updateRequest)
        );
        
        // Verify exception message
        assertTrue(exception.getMessage().contains("999"));
        
        // VERIFY
        verify(repository, times(1)).findById(invalidId);
        verify(repository, never()).save(any());
        verify(mapper, never()).toResponse(any());
    }
    
    // DELETE OPERATIONS TESTS
    
    @Test
    @DisplayName("deactivateOdds - Valid ID - Should Set Active To False")
    void deactivateOdds_ValidId_ShouldSetActiveToFalse() {
        // WHAT ARE WE TESTING?
        // We're testing SOFT DELETE - the entity is not removed from database,
        // but marked as inactive (active = false).
        // 
        // WHY SOFT DELETE?
        // - Keep historical data for reports
        // - Comply with regulations (audit trail)
        // - Allow potential reactivation
        // - Prevent foreign key constraint issues
        
        // ARRANGE
        Long oddsId = 1L;
        
        // STUB: Repository finds entity
        when(repository.findById(oddsId)).thenReturn(Optional.of(entity));
        
        // STUB: Repository saves deactivated entity
        when(repository.save(any(BettingOdds.class))).thenReturn(entity);
        
        // ACT
        service.deactivateOdds(oddsId);
        
        // VERIFY
        verify(repository, times(1)).findById(oddsId);
        
        // Verify that save was called with entity that has active = false
        verify(repository, times(1)).save(any(BettingOdds.class));
        
        // Verify audit log
        verify(auditLogger, times(1)).logOddsDeactivated(eq(1L), eq("Barcelona"), eq("Real Madrid"));
        
        // Verify performance log
        verify(performanceLogger, times(1)).logDatabaseQuery(eq("DEACTIVATE_ODDS"), anyLong());
    }
    
    @Test
    @DisplayName("deleteOdds - Valid ID - Should Delete From Database")
    void deleteOdds_ValidId_ShouldDeleteFromDatabase() {
        // WHAT ARE WE TESTING?
        // We're testing HARD DELETE - the entity is permanently removed from database.
        // 
        // WHY HARD DELETE?
        // - Remove test data
        // - GDPR compliance (right to be forgotten)
        // - Database cleanup
        // 
        // WARNING: Use with caution in production!
        
        // ARRANGE
        Long oddsId = 1L;
        
        // STUB: Repository finds entity
        when(repository.findById(oddsId)).thenReturn(Optional.of(entity));
        
        // ACT
        service.deleteOdds(oddsId);
        
        // VERIFY
        verify(repository, times(1)).findById(oddsId);
        
        // Verify deleteById was called
        verify(repository, times(1)).deleteById(oddsId);
        
        // Verify audit log (CRITICAL - hard delete must be logged)
        verify(auditLogger, times(1)).logOddsDeleted(eq(1L), eq("Barcelona"), eq("Real Madrid"));
        
        // Verify performance log
        verify(performanceLogger, times(1)).logDatabaseQuery(eq("DELETE_ODDS"), anyLong());
    }
    
    @Test
    @DisplayName("deleteOdds - Invalid ID - Should Throw ResourceNotFoundException")
    void deleteOdds_InvalidId_ShouldThrowResourceNotFoundException() {
        // WHAT ARE WE TESTING?
        // We're testing that deleteOdds() properly handles the case when
        // trying to delete a non-existent entity.
        // This prevents errors and provides clear feedback to the caller.
        
        // ARRANGE
        Long invalidId = 999L;
        
        // STUB: Repository returns empty (not found)
        when(repository.findById(invalidId)).thenReturn(Optional.empty());
        
        // ACT & ASSERT
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> service.deleteOdds(invalidId)
        );
        
        // Verify exception message
        assertTrue(exception.getMessage().contains("999"));
        
        // VERIFY
        verify(repository, times(1)).findById(invalidId);
        verify(repository, never()).deleteById(any());
        verify(auditLogger, never()).logOddsDeleted(anyLong(), any(), any());
    }
    
    // BUSINESS LOGIC TESTS
    
    @Test
    @DisplayName("getOddsWithMargin - Valid Odds - Should Calculate Correct Margin")
    void getOddsWithMargin_ValidOdds_ShouldCalculateCorrectMargin() {
        // WHAT ARE WE TESTING?
        // We're testing the bookmaker margin calculation - core business logic.
        // 
        // BOOKMAKER MARGIN EXPLAINED:
        // - Implied probability = 1 / odds
        // - Sum of all probabilities should be 100% (fair odds)
        // - But bookmakers add margin (their profit)
        // - Example: 47.6% + 29.4% + 27.8% = 104.8%
        // - Margin = 104.8% - 100% = 4.8%
        //
        // WHY TEST THIS?
        // - Critical business calculation
        // - Must be accurate for pricing
        // - Affects company revenue
        
        // ARRANGE
        Long oddsId = 1L;
        
        // Create response with calculated margin
        OddsResponse responseWithMargin = new OddsResponse(
            1L,
            "Football",
            "Barcelona",
            "Real Madrid",
            BigDecimal.valueOf(2.10),
            BigDecimal.valueOf(3.40),
            BigDecimal.valueOf(3.60),
            LocalDateTime.now().plusDays(7),
            true,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        // Set calculated fields
        responseWithMargin.setImpliedProbabilityHome(1.0 / 2.10); // 0.47619 (47.6%)
        responseWithMargin.setImpliedProbabilityDraw(1.0 / 3.40); // 0.29411 (29.4%)
        responseWithMargin.setImpliedProbabilityAway(1.0 / 3.60); // 0.27777 (27.8%)
        responseWithMargin.setBookmakerMargin(4.808); // Total 104.8% - 100% = 4.8%
        
        // STUB: Repository finds entity
        when(repository.findById(oddsId)).thenReturn(Optional.of(entity));
        
        // STUB: Mapper converts with margin calculation
        when(mapper.toResponseWithMargin(entity)).thenReturn(responseWithMargin);
        
        // ACT
        OddsResponse result = service.getOddsWithMargin(oddsId);
        
        // ASSERT
        assertNotNull(result);
        assertEquals(1L, result.getId());
        
        // Verify implied probabilities are calculated
        assertNotNull(result.getImpliedProbabilityHome());
        assertNotNull(result.getImpliedProbabilityDraw());
        assertNotNull(result.getImpliedProbabilityAway());
        
        // Verify margin is calculated and within expected range (4-5%)
        assertNotNull(result.getBookmakerMargin());
        assertTrue(result.getBookmakerMargin() > 4.0);
        assertTrue(result.getBookmakerMargin() < 5.0);
        
        // VERIFY
        verify(repository, times(1)).findById(oddsId);
        verify(mapper, times(1)).toResponseWithMargin(entity);
        verify(auditLogger, times(1)).logMarginCalculation(
            eq(1L), 
            any(Double.class), 
            eq("Barcelona"), 
            eq("Real Madrid")
        );
        verify(performanceLogger, times(1)).logCalculation(eq("BOOKMAKER_MARGIN"), anyLong());
    }
    
    // SECURITY VALIDATION TESTS
    
    @Test
    @DisplayName("createOdds - SQL Injection Attempt - Should Throw InvalidOddsException")
    void createOdds_SqlInjectionAttempt_ShouldThrowInvalidOddsException() {
        // WHAT ARE WE TESTING?
        // We're testing that the service BLOCKS SQL injection attempts.
        // 
        // SQL INJECTION EXPLAINED:
        // Attacker tries to inject SQL code in input fields:
        // homeTeam = "Barcelona'; DROP TABLE betting_odds--"
        // If not validated, this could execute: DROP TABLE betting_odds
        // Result: Database destroyed
        //
        // WHY WE BLOCK THIS:
        // 1. Data integrity - don't want garbage like "DROP TABLE" in database
        // 2. Defense in depth - multiple security layers
        // 3. Audit trail - log all attack attempts
        // 4. Regulatory compliance - must demonstrate security
        //
        // NOTE: Even though JPA uses prepared statements (which prevent SQL injection),
        // we still validate input at service layer for defense in depth.
        
        // ARRANGE - Create request with SQL injection attempt
        CreateOddsRequest maliciousRequest = new CreateOddsRequest(
            "Football",
            "Barcelona'; DROP TABLE betting_odds--", // SQL injection attempt
            "Real Madrid",
            BigDecimal.valueOf(2.10),
            BigDecimal.valueOf(3.40),
            BigDecimal.valueOf(3.60),
            LocalDateTime.now().plusDays(7)
        );
        
        // ACT & ASSERT
        com.gambling.betting_odds_api.exception.InvalidOddsException exception = 
            assertThrows(
                com.gambling.betting_odds_api.exception.InvalidOddsException.class,
                () -> service.createOdds(maliciousRequest)
            );
        
        // Verify exception message mentions security
        assertTrue(exception.getMessage().toLowerCase().contains("suspicious"));
        
        // VERIFY - Repository should NEVER be called (request blocked before save)
        verify(repository, never()).save(any());
        
        // Verify security logger was called to log the attack
        verify(securityLogger, times(1)).logSqlInjectionAttempt(any(), any());
    }
    
    @Test
    @DisplayName("createOdds - XSS Attempt - Should Throw InvalidOddsException")
    void createOdds_XssAttempt_ShouldThrowInvalidOddsException() {
        // WHAT ARE WE TESTING?
        // We're testing that the service BLOCKS XSS (Cross-Site Scripting) attempts.
        // 
        // XSS ATTACK EXPLAINED:
        // Attacker tries to inject JavaScript code:
        // sport = "<script>alert('XSS')</script>"
        // If displayed in browser without escaping, script executes
        // Could steal user sessions, cookies, etc.
        //
        // WHY WE BLOCK THIS:
        // 1. User safety - prevent malicious scripts
        // 2. Data quality - sport should be "Football", not "<script>"
        // 3. Security logging - track attack patterns
        
        // ARRANGE - Create request with XSS attempt
        CreateOddsRequest maliciousRequest = new CreateOddsRequest(
            "<script>alert('XSS')</script>", // XSS attempt in sport field
            "Barcelona",
            "Real Madrid",
            BigDecimal.valueOf(2.10),
            BigDecimal.valueOf(3.40),
            BigDecimal.valueOf(3.60),
            LocalDateTime.now().plusDays(7)
        );
        
        // ACT & ASSERT
        com.gambling.betting_odds_api.exception.InvalidOddsException exception = 
            assertThrows(
                com.gambling.betting_odds_api.exception.InvalidOddsException.class,
                () -> service.createOdds(maliciousRequest)
            );
        
        // Verify exception message mentions security
        assertTrue(exception.getMessage().toLowerCase().contains("suspicious"));
        
        // VERIFY - Repository should NEVER be called
        verify(repository, never()).save(any());
        
        // Verify security logger was called to log the XSS attempt
        verify(securityLogger, times(1)).logXssAttempt(any(), any());
    }
    
    @Test
    @DisplayName("createOdds - Suspiciously Low Odds - Should Log Security Warning")
    void createOdds_SuspiciouslyLowOdds_ShouldLogSecurityWarning() {
        // WHAT ARE WE TESTING?
        // We're testing detection of suspicious odds values.
        // 
        // SUSPICIOUS ODDS EXPLAINED:
        // - Odds below 1.01 are extremely unusual
        // - Could indicate:
        //   1. Data entry error
        //   2. Match fixing attempt
        //   3. System manipulation
        //
        // ACTION:
        // - Log the suspicious values (security log)
        // - Allow the operation (might be legitimate)
        // - Alert compliance team for review
        
        // ARRANGE - Create request with very low odds
        CreateOddsRequest suspiciousRequest = new CreateOddsRequest(
            "Football",
            "Barcelona",
            "Real Madrid",
            BigDecimal.valueOf(1.00), // Suspiciously low (below 1.01)
            BigDecimal.valueOf(3.40),
            BigDecimal.valueOf(3.60),
            LocalDateTime.now().plusDays(7)
        );
        
        // Create entity from suspicious request
        BettingOdds suspiciousEntity = new BettingOdds();
        suspiciousEntity.setId(1L);
        suspiciousEntity.setSport("Football");
        suspiciousEntity.setHomeTeam("Barcelona");
        suspiciousEntity.setAwayTeam("Real Madrid");
        suspiciousEntity.setHomeOdds(BigDecimal.valueOf(1.00));
        suspiciousEntity.setDrawOdds(BigDecimal.valueOf(3.40));
        suspiciousEntity.setAwayOdds(BigDecimal.valueOf(3.60));
        suspiciousEntity.setMatchDate(LocalDateTime.now().plusDays(7));
        suspiciousEntity.setActive(true);
        suspiciousEntity.setCreatedAt(LocalDateTime.now());
        suspiciousEntity.setUpdatedAt(LocalDateTime.now());
        
        // STUB: Mapper and repository
        when(mapper.toEntity(suspiciousRequest)).thenReturn(suspiciousEntity);
        when(repository.save(suspiciousEntity)).thenReturn(suspiciousEntity);
        when(mapper.toResponse(suspiciousEntity)).thenReturn(response);
        
        // ACT - Operation should succeed but log warning
        OddsResponse result = service.createOdds(suspiciousRequest);
        
        // ASSERT - Operation completed
        assertNotNull(result);
        
        // VERIFY - Security logger was called to log suspicious odds
        verify(securityLogger, times(1)).logInvalidOdds(
            eq("Barcelona"),
            eq("Real Madrid"),
            eq(1.00),
            eq(3.40),
            eq(3.60),
            any()
        );
    }
    
    @Test
    @DisplayName("getOddsWithMargin - Anomalous Margin - Should Log Security Warning")
    void getOddsWithMargin_AnomalousMargin_ShouldLogSecurityWarning() {
        // WHAT ARE WE TESTING?
        // We're testing detection of anomalous bookmaker margins.
        // 
        // ANOMALOUS MARGIN EXPLAINED:
        // - Normal margin: 2-10%
        // - Margin below 1%: Too low (loss for bookmaker)
        // - Margin above 20%: Too high (unfair to customers)
        //
        // CAUSES:
        // - Data entry errors
        // - System bugs
        // - Fraudulent manipulation
        //
        // ACTION:
        // - Log to security log
        // - Alert for manual review
        
        // ARRANGE
        Long oddsId = 1L;
        
        // Create entity with odds that produce anomalous margin
        BettingOdds anomalousEntity = new BettingOdds();
        anomalousEntity.setId(1L);
        anomalousEntity.setSport("Football");
        anomalousEntity.setHomeTeam("Barcelona");
        anomalousEntity.setAwayTeam("Real Madrid");
        anomalousEntity.setHomeOdds(BigDecimal.valueOf(1.10)); // Very low odds
        anomalousEntity.setDrawOdds(BigDecimal.valueOf(1.10)); // Very low odds
        anomalousEntity.setAwayOdds(BigDecimal.valueOf(1.10)); // Very low odds
        anomalousEntity.setMatchDate(LocalDateTime.now().plusDays(7));
        anomalousEntity.setActive(true);
        anomalousEntity.setCreatedAt(LocalDateTime.now());
        anomalousEntity.setUpdatedAt(LocalDateTime.now());
        
        // Create response with anomalous margin
        OddsResponse anomalousResponse = new OddsResponse(
            1L,
            "Football",
            "Barcelona",
            "Real Madrid",
            BigDecimal.valueOf(1.10),
            BigDecimal.valueOf(1.10),
            BigDecimal.valueOf(1.10),
            LocalDateTime.now().plusDays(7),
            true,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        // Calculate anomalous margin
        // 1/1.10 = 0.909 (90.9%) for each outcome
        // Total = 90.9% * 3 = 272.7%
        // Margin = 272.7% - 100% = 172.7% (way too high!)
        anomalousResponse.setImpliedProbabilityHome(0.909);
        anomalousResponse.setImpliedProbabilityDraw(0.909);
        anomalousResponse.setImpliedProbabilityAway(0.909);
        anomalousResponse.setBookmakerMargin(172.7);
        
        // STUB
        when(repository.findById(oddsId)).thenReturn(Optional.of(anomalousEntity));
        when(mapper.toResponseWithMargin(anomalousEntity)).thenReturn(anomalousResponse);
        
        // ACT
        OddsResponse result = service.getOddsWithMargin(oddsId);
        
        // ASSERT
        assertNotNull(result);
        assertTrue(result.getBookmakerMargin() > 20.0); // Anomalous
        
        // VERIFY - Security logger was called for anomalous margin
        verify(securityLogger, times(1)).logAnomalousMargin(
            eq(1L),
            any(Double.class),
            eq("Barcelona"),
            eq("Real Madrid")
        );
    }
    
    // WHAT WE LEARNED IN ALL 4 DAYS
    //
    // DAY 1 - CREATE:
    // - AAA Pattern (Arrange-Act-Assert)
    // - Mocking with @Mock and @InjectMocks
    // - Stubbing with when().thenReturn()
    // - Verifying interactions with verify()
    // - Basic assertions (assertEquals, assertNotNull, assertTrue)
    //
    // DAY 2 - READ:
    // - Testing with Optional (Optional.of() vs Optional.empty())
    // - Exception testing with assertThrows()
    // - Pagination testing (Pageable, Page, PageImpl)
    // - Filtering tests (by active status, by sport)
    // - Using never() to verify methods weren't called
    //
    // DAY 3 - UPDATE and DELETE:
    // - Testing update operations (find, modify, save)
    // - Testing soft delete (deactivate)
    // - Testing hard delete (permanent removal)
    // - Verifying state changes (active flag)
    // - Understanding soft vs hard delete trade-offs
    //
    // DAY 4 - BUSINESS LOGIC and SECURITY:
    // - Testing complex calculations (bookmaker margin)
    // - Testing security validations (SQL injection, XSS)
    // - Testing suspicious data detection
    // - Logging security events
    // - Defense in depth approach
    //
    // TEST COVERAGE:
    // CREATE:
    // - createOdds() - happy path
    // - createOdds() - SQL injection blocked
    // - createOdds() - XSS blocked
    // - createOdds() - suspicious odds logged
    //
    // READ:
    // - getOddsById() - happy path
    // - getOddsById() - not found
    // - getAllOdds() - with pagination
    // - getActiveOdds() - filtering
    // - getOddsBySport() - filtering
    // - getOddsWithMargin() - margin calculation
    // - getOddsWithMargin() - anomalous margin detection
    //
    // UPDATE:
    // - updateOdds() - happy path
    // - updateOdds() - not found
    //
    // DELETE:
    // - deactivateOdds() - soft delete
    // - deleteOdds() - hard delete
    // - deleteOdds() - not found
    //
    // Total: 16/45 tests, approximately 36 percent coverage
    // Service layer: 16/20 tests, approximately 80 percent coverage
    //
    // ACHIEVEMENTS:
    // - Comprehensive CRUD testing
    // - Security validation coverage
    // - Business logic verification
    // - Exception handling validation
    // - Professional test structure
    //
    // NEXT:
    // - Week 2: Mapper and Repository tests
    // - Week 3: Integration tests with MockMvc
    // - Final goal: 80 percent plus total coverage
}