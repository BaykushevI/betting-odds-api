package com.gambling.betting_odds_api.service;

// ═══════════════════════════════════════════════════════════════════════════
// INTERNAL PROJECT IMPORTS
// ═══════════════════════════════════════════════════════════════════════════
// DTOs - Data Transfer Objects for API layer
import com.gambling.betting_odds_api.dto.CreateOddsRequest;  // Request DTO for creating odds
import com.gambling.betting_odds_api.dto.OddsResponse;       // Response DTO for odds data
import com.gambling.betting_odds_api.dto.PageResponse;       // Generic paginated response wrapper
import com.gambling.betting_odds_api.dto.UpdateOddsRequest;  // Request DTO for updating odds

// Exceptions - Custom error handling
import com.gambling.betting_odds_api.exception.ResourceNotFoundException; // 404 Not Found exception

// Logging - Professional logging system
import com.gambling.betting_odds_api.logging.AuditLogger;
import com.gambling.betting_odds_api.logging.PerformanceLogger;
import com.gambling.betting_odds_api.logging.SecurityLogger;

// Mapper - DTO ↔ Entity conversion
import com.gambling.betting_odds_api.mapper.OddsMapper; // Converts between DTOs and Entities

// Model - Database entity
import com.gambling.betting_odds_api.model.BettingOdds; // JPA entity representing betting_odds table

// Repository - Database access layer
import com.gambling.betting_odds_api.repository.BettingOddsRepository; // Spring Data JPA repository

// ═══════════════════════════════════════════════════════════════════════════
// LOMBOK - Reduces boilerplate code
// ═══════════════════════════════════════════════════════════════════════════
import lombok.RequiredArgsConstructor; // Auto-generates constructor for final fields
import lombok.extern.slf4j.Slf4j;      // Auto-generates Logger instance (log variable)

// ═══════════════════════════════════════════════════════════════════════════
// SPRING DATA - Database operations with pagination
// ═══════════════════════════════════════════════════════════════════════════
import org.springframework.data.domain.Page;     // Wrapper for paginated query results
import org.springframework.data.domain.Pageable; // Pagination and sorting parameters

// ═══════════════════════════════════════════════════════════════════════════
// SPRING FRAMEWORK - Core functionality
// ═══════════════════════════════════════════════════════════════════════════
import org.springframework.stereotype.Service;                  // Marks class as service component
import org.springframework.transaction.annotation.Transactional; // Database transaction management

// ═══════════════════════════════════════════════════════════════════════════
// JAVA STANDARD LIBRARY - Core Java classes
// ═══════════════════════════════════════════════════════════════════════════
import java.time.LocalDateTime; // Date/time operations (for upcoming matches query)

/**
 * BettingOddsService - Business logic for betting odds operations.
 * 
 * ═══════════════════════════════════════════════════════════════════════════
 * SERVICE LAYER RESPONSIBILITIES:
 * ═══════════════════════════════════════════════════════════════════════════
 * 
 * 1. BUSINESS LOGIC
 *    - Contains ALL business rules and calculations
 *    - Example: Bookmaker margin calculation
 *    - Example: Validation that odds are within acceptable range
 * 
 * 2. TRANSACTION MANAGEMENT
 *    - @Transactional ensures ACID properties
 *    - If operation fails, database changes are rolled back
 *    - Critical for data consistency
 * 
 * 3. ORCHESTRATION
 *    - Coordinates multiple repository calls if needed
 *    - Manages data flow between layers
 *    - Converts entities to DTOs (via mapper)
 * 
 * 4. LOGGING
 *    - Detailed logging of operations
 *    - Performance tracking
 *    - Audit trail for compliance
 * 
 * ═══════════════════════════════════════════════════════════════════════════
 * WHY SERVICE LAYER EXISTS:
 * ═══════════════════════════════════════════════════════════════════════════
 * 
 * - SEPARATION OF CONCERNS: Controller handles HTTP, Service handles business
 * - REUSABILITY: Same service can be called from multiple controllers
 * - TESTABILITY: Easy to unit test business logic without HTTP layer
 * - TRANSACTION BOUNDARIES: Define where transactions start/end
 * 
 * ═══════════════════════════════════════════════════════════════════════════
 */
@Service                  // Spring component - auto-discovered and registered as bean
@RequiredArgsConstructor  // Lombok: generates constructor for final fields (dependency injection)
@Slf4j                    // Lombok: generates Logger log = LoggerFactory.getLogger(BettingOddsService.class)
public class BettingOddsService {
    
    // ═══════════════════════════════════════════════════════════════════════
    // DEPENDENCIES - Injected via constructor (thanks to @RequiredArgsConstructor)
    // ═══════════════════════════════════════════════════════════════════════
    
    private final BettingOddsRepository repository; // Database access
    private final OddsMapper mapper;                 // DTO ↔ Entity conversion
    
    private final AuditLogger auditLogger;           // Audit logging
    private final PerformanceLogger performanceLogger; // Performance logging
    private final SecurityLogger securityLogger;     // Security logging

    // Future: Logger classes will be added here in next commits
    // private final AuditLogger auditLogger;
    // private final PerformanceLogger performanceLogger;
    // private final SecurityLogger securityLogger;
    
    // ═══════════════════════════════════════════════════════════════════════
    // CREATE OPERATIONS
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Create new betting odds.
     * 
     * Transaction Flow:
     * 1. Convert DTO → Entity (via mapper)
     * 2. Save to database
     * 3. Convert saved Entity → Response DTO
     * 4. Return to controller
     * 
     * @Transactional ensures:
     * - If save fails, no partial data is committed
     * - Automatic rollback on exceptions
     * - Database connection is managed automatically
     * 
     * @param request CreateOddsRequest DTO with odds data
     * @return OddsResponse DTO with saved data (including generated ID)
     */
    @Transactional
    public OddsResponse createOdds(CreateOddsRequest request) {
        
        long startTime = PerformanceLogger.startTiming();

        log.infor("createing new betting odds for match: {} vs {}",
                request.getHomeTeam(), request.getAwayTeam());

        log.debug("Full odds details: sport={}, homeOdds={}, drawOdds={}, awayOdds={}, matchDate={}",
                request.getSport(), request.getHomeOdds(),
                request.getDrawOdds(), request.getAwayOdds(),
                request.getMatchDate());

        try{
            validateOddsForSecurity(request);

            BettingOdds odds = mapper.toEntity(request);
            BettingOdds saved = repository.save(odds);
            log.info("Successfully created betting odds with ID: {}", saved.getId());

            auditLogger.logOddsCreated(
                saved.getId(), 
                saved.getSport(),
                saved.getHomeTeam(),
                saved.getAwayTeam()
            );

            long duration = PerformanceLogger.endTiming(startTime);
            performanceLogger.logDatabaseQuery("CREATE_ODDS", duration);

            return mapper.toResponse(saved);
        } catch (Exception ex){
            log.error("Error creating betting odds: {}", ex.getMessage());
            throw ex;
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // READ OPERATIONS (Queries)
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Get all odds with optional pagination.
     * 
     * Read-only operation - no @Transactional needed.
     * Spring Data JPA handles query execution and pagination automatically.
     * 
     * @param pageable Pagination parameters (page, size, sort)
     * @return PageResponse containing list of odds and pagination metadata
     */
    public PageResponse<OddsResponse> getAllOdds(Pageable pageable) {
        // Query database (Spring Data JPA generates SQL automatically)
        Page<BettingOdds> page = repository.findAll(pageable);
        
        // Convert Page<Entity> to Page<DTO>
        // .map() applies mapper.toResponse() to each entity in the page
        Page<OddsResponse> responsePage = page.map(mapper::toResponse);
        
        // Wrap in our custom PageResponse (adds consistent structure)
        return PageResponse.of(responsePage);
    }
    
    /**
     * Get only active odds with pagination.
     * 
     * @param pageable Pagination parameters
     * @return PageResponse containing only active odds
     */
    public PageResponse<OddsResponse> getActiveOdds(Pageable pageable) {
        Page<BettingOdds> page = repository.findByActiveTrue(pageable);
        Page<OddsResponse> responsePage = page.map(mapper::toResponse);
        return PageResponse.of(responsePage);
    }
    
    /**
     * Get odds by ID.
     * 
     * Single record - no pagination needed.
     * Throws ResourceNotFoundException if not found (handled by GlobalExceptionHandler).
     * 
     * @param id Unique identifier
     * @return OddsResponse DTO
     * @throws ResourceNotFoundException if odds not found
     */
    public OddsResponse getOddsById(Long id) {
        // findById returns Optional<BettingOdds>
        // orElseThrow() returns entity if present, or throws exception if empty
        BettingOdds odds = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Betting Odds", id));
        
        return mapper.toResponse(odds);
    }
    
    /**
     * Get odds by sport (only active) with pagination.
     * 
     * @param sport Sport name (e.g., "Football", "Basketball")
     * @param pageable Pagination parameters
     * @return PageResponse containing odds for specified sport
     */
    public PageResponse<OddsResponse> getOddsBySport(String sport, Pageable pageable) {
        Page<BettingOdds> page = repository.findBySportAndActiveTrue(sport, pageable);
        Page<OddsResponse> responsePage = page.map(mapper::toResponse);
        return PageResponse.of(responsePage);
    }
    
    /**
     * Get upcoming matches (future dates only) with pagination.
     * 
     * Uses custom query in repository:
     * @Query("SELECT o FROM BettingOdds o WHERE o.matchDate > :currentDate")
     * 
     * @param pageable Pagination parameters
     * @return PageResponse containing upcoming matches
     */
    public PageResponse<OddsResponse> getUpcomingMatches(Pageable pageable) {
        // Pass current timestamp to query
        Page<BettingOdds> page = repository.findUpcomingMatches(
                LocalDateTime.now(), pageable);
        Page<OddsResponse> responsePage = page.map(mapper::toResponse);
        return PageResponse.of(responsePage);
    }
    
    /**
     * Get matches for specific team (home or away) with pagination.
     * 
     * Uses custom query in repository:
     * @Query("SELECT o FROM BettingOdds o WHERE o.homeTeam = :teamName OR o.awayTeam = :teamName")
     * 
     * @param teamName Team name to search for
     * @param pageable Pagination parameters
     * @return PageResponse containing matches for team
     */
    public PageResponse<OddsResponse> getMatchesForTeam(String teamName, Pageable pageable) {
        Page<BettingOdds> page = repository.findByTeam(teamName, pageable);
        Page<OddsResponse> responsePage = page.map(mapper::toResponse);
        return PageResponse.of(responsePage);
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // UPDATE OPERATIONS
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Update existing odds.
     * 
     * Transaction Flow:
     * 1. Find existing entity (or throw exception)
     * 2. Update fields from DTO (via mapper)
     * 3. Save updated entity (JPA updates updatedAt timestamp automatically)
     * 4. Return updated DTO
     * 
     * @Transactional ensures atomic operation.
     * 
     * @param id Unique identifier of odds to update
     * @param request UpdateOddsRequest DTO with new values
     * @return OddsResponse DTO with updated data
     * @throws ResourceNotFoundException if odds not found
     */
    @Transactional
    public OddsResponse updateOdds(Long id, UpdateOddsRequest request) {
        log.info("Updating odds with ID: {}", id);

        // Find existing entity (or throw exception)
        BettingOdds existing = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Odds not found with ID: {}", id);
                    return new ResourceNotFoundException("Betting Odds", id);
                });
        
        // Log what we found
        log.debug("Found existing odds: {} vs {}", 
                existing.getHomeTeam(), existing.getAwayTeam());
        
        // Log what we're changing to
        log.debug("Updating with new values: homeOdds={}, drawOdds={}, awayOdds={}",
                request.getHomeOdds(), request.getDrawOdds(), request.getAwayOdds());
        
        // Update entity from DTO (mapper handles field mapping)
        mapper.updateEntityFromDto(existing, request);
        
        // Save updated entity
        // JPA's @PreUpdate in entity automatically sets updatedAt timestamp
        BettingOdds updated = repository.save(existing);
        
        log.info("Successfully updated odds with ID: {}", id);

        return mapper.toResponse(updated);
    }
    
    /**
     * Deactivate odds (soft delete).
     * 
     * Sets active = false instead of deleting from database.
     * Preserves data for audit trail and historical analysis.
     * 
     * @Transactional ensures atomic operation.
     * 
     * @param id Unique identifier of odds to deactivate
     * @throws ResourceNotFoundException if odds not found
     */
    @Transactional
    public void deactivateOdds(Long id) {
        // Find existing entity
        BettingOdds odds = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Betting Odds", id));
        
        // Set to inactive
        odds.setActive(false);
        
        // Save (triggers @PreUpdate, sets updatedAt timestamp)
        repository.save(odds);
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // DELETE OPERATIONS
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Delete odds permanently (hard delete).
     * 
     * ⚠️ WARNING: This is PERMANENT and IRREVERSIBLE!
     * - No audit trail after deletion
     * - Cannot recover data
     * - Violates gambling industry best practices
     * 
     * Should be RARE in production. Prefer soft delete (deactivate).
     * 
     * @Transactional ensures atomic operation.
     * 
     * @param id Unique identifier of odds to delete
     * @throws ResourceNotFoundException if odds not found
     */
    @Transactional
    public void deleteOdds(Long id) {
        // WARN level because hard delete is serious
        log.warn("Attempting to delete odds with ID: {} (HARD DELETE)", id);

        // Check if exists (throws exception if not)
        if (!repository.existsById(id)) {
            log.error("Cannot delete - odds not found with ID: {}", id);
            throw new ResourceNotFoundException("Betting Odds", id);
        }
        
        // Permanently delete from database
        repository.deleteById(id);
        
        log.info("Successfully deleted odds with ID: {}", id);
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // BUSINESS LOGIC - Calculations
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Get odds with calculated bookmaker margin.
     * 
     * Bookmaker Margin Formula:
     * 1. Calculate implied probabilities:
     *    - Home: 1 / homeOdds
     *    - Draw: 1 / drawOdds
     *    - Away: 1 / awayOdds
     * 
     * 2. Sum probabilities (will be > 100% due to margin)
     * 
     * 3. Margin = Sum - 100%
     * 
     * Example:
     * homeOdds=2.10, drawOdds=3.40, awayOdds=3.60
     * impliedHome = 1/2.10 = 47.6%
     * impliedDraw = 1/3.40 = 29.4%
     * impliedAway = 1/3.60 = 27.8%
     * Sum = 104.8%
     * Margin = 4.8% (bookmaker's profit)
     * 
     * @param id Unique identifier
     * @return OddsResponse with calculated margin fields populated
     * @throws ResourceNotFoundException if odds not found
     */
    public OddsResponse getOddsWithMargin(Long id) {
        BettingOdds odds = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Betting Odds", id));
        
        // Mapper handles calculation logic
        return mapper.toResponseWithMargin(odds);
    }
}