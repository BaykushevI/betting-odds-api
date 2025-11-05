package com.gambling.betting_odds_api.controller;

// ═══════════════════════════════════════════════════════════════════════════
// INTERNAL PROJECT IMPORTS
// ═══════════════════════════════════════════════════════════════════════════
// DTOs - Data Transfer Objects for request/response
import com.gambling.betting_odds_api.dto.CreateOddsRequest;  // Request DTO for creating odds
import com.gambling.betting_odds_api.dto.OddsResponse;       // Response DTO for odds data
import com.gambling.betting_odds_api.dto.PageResponse;       // Generic paginated response wrapper
import com.gambling.betting_odds_api.dto.UpdateOddsRequest;  // Request DTO for updating odds

// Service Layer - Business logic
import com.gambling.betting_odds_api.service.BettingOddsService; // Main service for odds operations

// ═══════════════════════════════════════════════════════════════════════════
// SWAGGER/OPENAPI IMPORTS - API Documentation
// ═══════════════════════════════════════════════════════════════════════════
import io.swagger.v3.oas.annotations.Operation;              // Describes what endpoint does
import io.swagger.v3.oas.annotations.Parameter;              // Describes endpoint parameters
import io.swagger.v3.oas.annotations.media.Content;          // Describes response content type
import io.swagger.v3.oas.annotations.media.Schema;           // Links to DTO schema
import io.swagger.v3.oas.annotations.responses.ApiResponse;  // Single response documentation
import io.swagger.v3.oas.annotations.responses.ApiResponses; // Multiple responses documentation

// ═══════════════════════════════════════════════════════════════════════════
// JAKARTA IMPORTS - Java EE Standard (formerly javax)
// ═══════════════════════════════════════════════════════════════════════════
import jakarta.validation.Valid; // Triggers validation on @RequestBody

// ═══════════════════════════════════════════════════════════════════════════
// LOMBOK - Reduces boilerplate code
// ═══════════════════════════════════════════════════════════════════════════
import lombok.RequiredArgsConstructor; // Auto-generates constructor for final fields
import lombok.extern.slf4j.Slf4j;      // Auto-generates Logger instance (log variable)

// ═══════════════════════════════════════════════════════════════════════════
// SPRING DATA - Pagination and Sorting
// ═══════════════════════════════════════════════════════════════════════════
import org.springframework.data.domain.PageRequest; // Combines page number, size, and sort
import org.springframework.data.domain.Pageable;    // Interface for pagination parameters
import org.springframework.data.domain.Sort;        // Sorting configuration

// ═══════════════════════════════════════════════════════════════════════════
// SPRING WEB - REST API Functionality
// ═══════════════════════════════════════════════════════════════════════════
import org.springframework.http.HttpStatus;      // HTTP status codes (200, 201, 404, etc.)
import org.springframework.http.ResponseEntity;  // Wrapper for HTTP response with status and body
import org.springframework.web.bind.annotation.*; // REST annotations (@GetMapping, @PostMapping, etc.)

// ═══════════════════════════════════════════════════════════════════════════
// JAVA STANDARD LIBRARY - Core Java Classes
// ═══════════════════════════════════════════════════════════════════════════
import java.util.ArrayList; // Dynamic array for sort parameters
import java.util.HashMap;   // Key-value pairs for success messages
import java.util.List;      // Interface for ordered collections
import java.util.Map;       // Interface for key-value pairs

import org.springframework.security.access.prepost.PreAuthorize; // Role-based authorization
/**
 * BettingOddsController - REST API endpoints for managing betting odds.
 * 
 * ═══════════════════════════════════════════════════════════════════════════
 * CONTROLLER LAYER RESPONSIBILITIES:
 * ═══════════════════════════════════════════════════════════════════════════
 * 
 * 1. HTTP REQUEST HANDLING
 *    - Map URLs to methods (@GetMapping, @PostMapping, etc.)
 *    - Extract parameters from request (path variables, query params, body)
 *    - Validate input with @Valid annotation
 * 
 * 2. DELEGATE TO SERVICE LAYER
 *    - Controller does NOT contain business logic
 *    - All operations delegated to BettingOddsService
 *    - Controller = thin layer, Service = thick layer
 * 
 * 3. HTTP RESPONSE BUILDING
 *    - Convert service results to HTTP responses
 *    - Set appropriate HTTP status codes (200, 201, 404, etc.)
 *    - Return DTOs (never entities!)
 * 
 * 4. API DOCUMENTATION
 *    - Swagger annotations for interactive docs
 *    - Appears at http://localhost:8080/swagger-ui.html
 * 
 * ═══════════════════════════════════════════════════════════════════════════
 */
@RestController                      // Combines @Controller + @ResponseBody
@RequestMapping("/api/odds")         // Base path for all endpoints in this controller
@RequiredArgsConstructor             // Lombok: generates constructor for final fields
@Slf4j                               // Lombok: generates Logger log = LoggerFactory.getLogger(...)
public class BettingOddsController {
    
    // Dependency Injection via constructor (thanks to @RequiredArgsConstructor)
    private final BettingOddsService service;

    // ═══════════════════════════════════════════════════════════════════════
    // GET ENDPOINTS - Read Operations
    // ═══════════════════════════════════════════════════════════════════════
    @PreAuthorize("hasAnyRole('USER', 'BOOKMAKER', 'ADMIN')")
    /**
     * GET /api/odds - Retrieve all betting odds with pagination and sorting.
     * 
     * Examples:
     * - /api/odds                                      → All odds (unpaginated)
     * - /api/odds?page=0&size=10                       → First page, 10 items
     * - /api/odds?sort=matchDate,desc                  → Sorted by date descending
     * - /api/odds?page=1&sort=sport,asc&sort=homeOdds,desc → Multiple sort fields
     */
    @Operation(
        summary = "Get all betting odds",
        description = "Retrieve all betting odds with optional pagination and sorting. " +
                      "Use 'page' and 'size' for pagination. " +
                      "Use 'sort' in format 'property,direction' (e.g., 'matchDate,desc')."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully retrieved odds",
            content = @Content(schema = @Schema(implementation = PageResponse.class))
        )
    })
    @GetMapping
    public ResponseEntity<PageResponse<OddsResponse>> getAllOdds(
            @Parameter(description = "Page number (0-based index)", example = "0")
            @RequestParam(required = false) Integer page,
            
            @Parameter(description = "Number of items per page (max 100)", example = "20")
            @RequestParam(required = false) Integer size,
            
            @Parameter(
                description = "Sorting criteria in format 'property,direction'. " +
                              "Multiple sort parameters supported.", 
                example = "sport,asc"
            )
            @RequestParam(required = false) List<String> sort) {
        
        Pageable pageable = buildPageable(page, size, sort);
        PageResponse<OddsResponse> response = service.getAllOdds(pageable);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/odds/active - Get only active odds.
     */
    @PreAuthorize("hasAnyRole('USER', 'BOOKMAKER', 'ADMIN')")
    @GetMapping("/active")
    public ResponseEntity<PageResponse<OddsResponse>> getActiveOdds(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) List<String> sort) {
        
        Pageable pageable = buildPageable(page, size, sort);
        PageResponse<OddsResponse> response = service.getActiveOdds(pageable);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/odds/{id} - Get odds by ID.
     */
    @PreAuthorize("hasAnyRole('USER', 'BOOKMAKER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<OddsResponse> getOddsById(@PathVariable Long id) {
        OddsResponse odds = service.getOddsById(id);
        return ResponseEntity.ok(odds);
    }
    
    /**
     * GET /api/odds/sport/{sport} - Get odds by sport.
     */
    @PreAuthorize("hasAnyRole('USER', 'BOOKMAKER', 'ADMIN')")
    @GetMapping("/sport/{sport}")
    public ResponseEntity<PageResponse<OddsResponse>> getOddsBySport(
            @PathVariable String sport,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) List<String> sort) {
        
        Pageable pageable = buildPageable(page, size, sort);
        PageResponse<OddsResponse> response = service.getOddsBySport(sport, pageable);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/odds/upcoming - Get upcoming matches (future dates only).
     */
    @PreAuthorize("hasAnyRole('USER', 'BOOKMAKER', 'ADMIN')")
    @GetMapping("/upcoming")
    public ResponseEntity<PageResponse<OddsResponse>> getUpcomingMatches(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) List<String> sort) {
        
        Pageable pageable = buildPageable(page, size, sort);
        PageResponse<OddsResponse> response = service.getUpcomingMatches(pageable);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/odds/team/{teamName} - Get matches for specific team.
     */
    @PreAuthorize("hasAnyRole('USER', 'BOOKMAKER', 'ADMIN')")
    @GetMapping("/team/{teamName}")
    public ResponseEntity<PageResponse<OddsResponse>> getMatchesForTeam(
            @PathVariable String teamName,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) List<String> sort) {
        
        Pageable pageable = buildPageable(page, size, sort);
        PageResponse<OddsResponse> response = service.getMatchesForTeam(teamName, pageable);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/odds/{id}/margin - Calculate bookmaker margin for specific odds.
     */
    @PreAuthorize("hasAnyRole('USER', 'BOOKMAKER', 'ADMIN')")
    @GetMapping("/{id}/margin")
    public ResponseEntity<OddsResponse> getBookmakerMargin(@PathVariable Long id) {
        OddsResponse response = service.getOddsWithMargin(id);
        return ResponseEntity.ok(response);
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // POST ENDPOINT - Create Operation
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * POST /api/odds - Create new betting odds.
     * 
     * @Valid triggers validation rules from CreateOddsRequest
     * Returns 201 CREATED on success
     */
    @PreAuthorize("hasAnyRole('BOOKMAKER', 'ADMIN')")
    @Operation(
        summary = "Create new betting odds", 
        description = "Create a new odds entry for a sports match."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Odds created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<OddsResponse> createOdds(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Details of the odds to be created",
                required = true,
                content = @Content(schema = @Schema(implementation = CreateOddsRequest.class))
            )    
            @Valid @RequestBody CreateOddsRequest request) {
        
        // Basic logging at controller level (detailed logging in service)
        log.info("POST /api/odds - Creating odds for: {} vs {}", 
                request.getHomeTeam(), request.getAwayTeam());

        OddsResponse created = service.createOdds(request);

        log.info("POST /api/odds - Created odds with ID: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // PUT ENDPOINT - Update Operation
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * PUT /api/odds/{id} - Update existing odds.
     */
    @PreAuthorize("hasAnyRole('BOOKMAKER', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<OddsResponse> updateOdds(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateOddsRequest request) {
        
        OddsResponse updated = service.updateOdds(id, request);
        return ResponseEntity.ok(updated);
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // PATCH ENDPOINT - Soft Delete Operation
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * PATCH /api/odds/{id}/deactivate - Deactivate odds (soft delete).
     * 
     * Sets active=false instead of deleting from database.
     * Preferred over hard delete for audit trail.
     */
    @PreAuthorize("hasAnyRole('BOOKMAKER', 'ADMIN')")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateOdds(@PathVariable Long id) {
        service.deactivateOdds(id);
        return ResponseEntity.ok(createSuccessResponse("Odds deactivated successfully"));
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // DELETE ENDPOINT - Hard Delete Operation
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * DELETE /api/odds/{id} - Permanently delete odds.
     * 
     * ⚠️ WARNING: This is PERMANENT and IRREVERSIBLE!
     * Should be rare in production. Prefer soft delete (deactivate).
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteOdds(@PathVariable Long id) {
        service.deleteOdds(id);
        return ResponseEntity.ok(createSuccessResponse("Odds deleted successfully"));
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // HELPER METHODS - Private Utilities
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Create success response map.
     * 
     * @param message Success message
     * @return Map with "message" key
     */
    private Map<String, String> createSuccessResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return response;
    }
    
    /**
     * Build Pageable object from request parameters.
     * 
     * Handles three scenarios:
     * 1. No pagination params → return all (unpaged)
     * 2. Pagination with sort → paginated and sorted
     * 3. Pagination without sort → paginated, sorted by ID DESC (newest first)
     * 
     * @param page Page number (0-indexed)
     * @param size Items per page (default 20, max 100)
     * @param sort Sort parameters (format: "property,direction")
     * @return Pageable configuration
     */
    private Pageable buildPageable(Integer page, Integer size, List<String> sort) {
        // Scenario 1: No pagination requested → return all results
        if (page == null && size == null) {
            if (sort != null && !sort.isEmpty()) {
                return Pageable.unpaged(buildSort(sort));
            }
            return Pageable.unpaged();
        }
        
        // Scenario 2 & 3: Pagination requested
        // Validate and set defaults
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 20; // Default 20 items per page
        if (pageSize > 100) pageSize = 100; // Max 100 items per page
        
        // Build sort configuration
        Sort sortObj = (sort != null && !sort.isEmpty()) 
                ? buildSort(sort) 
                : Sort.by(Sort.Direction.DESC, "id"); // Default: newest first
        
        return PageRequest.of(pageNumber, pageSize, sortObj);
    }
    
    /**
     * Build Sort object from list of sort parameters.
     * 
     * Input format: ["sport,asc", "homeOdds,desc"]
     * After parsing: Sort by sport ASC, then by homeOdds DESC
     * 
     * Process:
     * 1. Flatten comma-separated values
     * 2. Validate pairs (property + direction)
     * 3. Create Sort.Order for each pair
     * 
     * @param sort List of sort parameters
     * @return Sort configuration
     * @throws IllegalArgumentException if invalid format
     */
    private Sort buildSort(List<String> sort) {
        // Step 1: Flatten comma-separated values
        // Input:  ["sport,asc", "homeOdds,desc"]
        // Output: ["sport", "asc", "homeOdds", "desc"]
        List<String> flattened = new ArrayList<>();
        for (String sortParam : sort) {
            String[] parts = sortParam.split(",");
            for (String part : parts) {
                flattened.add(part.trim());
            }
        }
        
        // Step 2: Validate pairs
        if (flattened.size() % 2 != 0) {
            throw new IllegalArgumentException(
                "Sort parameters must come in pairs (property, direction). " +
                "Got " + flattened.size() + " parameters after splitting."
            );
        }
        
        // Step 3: Process pairs into Sort.Order array
        int pairCount = flattened.size() / 2;
        Sort.Order[] orders = new Sort.Order[pairCount];
        
        for (int i = 0; i < flattened.size(); i += 2) {
            String property = flattened.get(i);
            String directionStr = flattened.get(i + 1);
            
            // Parse direction
            Sort.Direction direction;
            if ("desc".equalsIgnoreCase(directionStr)) {
                direction = Sort.Direction.DESC;
            } else if ("asc".equalsIgnoreCase(directionStr)) {
                direction = Sort.Direction.ASC;
            } else {
                throw new IllegalArgumentException(
                    "Invalid sort direction: '" + directionStr + "'. " +
                    "Must be 'asc' or 'desc'."
                );
            }
            
            orders[i / 2] = new Sort.Order(direction, property);
        }
        
        return Sort.by(orders);
    }
}