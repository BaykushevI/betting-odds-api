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
    
    // WHAT WE LEARNED SO FAR
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
    // TEST COVERAGE:
    // - createOdds()
    // - getOddsById() - happy path
    // - getOddsById() - not found
    // - getAllOdds() - with pagination
    // - getActiveOdds() - filtering
    // - getOddsBySport() - filtering
    //
    // Total: 6/45 tests, approximately 13 percent coverage
    //
    // NEXT (DAY 3):
    // - updateOdds() - happy path
    // - updateOdds() - not found
    // - deactivateOdds() - soft delete
    // - deleteOdds() - hard delete
    // - Security validation tests
}