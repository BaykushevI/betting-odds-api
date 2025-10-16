package com.gambling.betting_odds_api.controller;

import com.gambling.betting_odds_api.dto.CreateOddsRequest;
import com.gambling.betting_odds_api.dto.OddsResponse;
import com.gambling.betting_odds_api.dto.PageResponse;
import com.gambling.betting_odds_api.dto.UpdateOddsRequest;
import com.gambling.betting_odds_api.service.BettingOddsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/odds")
@RequiredArgsConstructor
public class BettingOddsController {
    
    private final BettingOddsService service;
    
    // GET /api/odds - Get all odds (supports pagination and sorting)
    // Examples:
    // /api/odds - get all (unpaginated)
    // /api/odds?page=0&size=10 - first page, 10 items
    // /api/odds?page=1&size=20&sort=matchDate,desc - second page, sorted by date descending
    // /api/odds?page=0&size=10&sort=sport,asc&sort=homeOdds,desc - multiple sort fields
    @GetMapping
    public ResponseEntity<PageResponse<OddsResponse>> getAllOdds(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String[] sort) {
        
        Pageable pageable = buildPageable(page, size, sort);
        PageResponse<OddsResponse> response = service.getAllOdds(pageable);
        return ResponseEntity.ok(response);
    }
    
    // GET /api/odds/active - Get only active odds (supports pagination)
    @GetMapping("/active")
    public ResponseEntity<PageResponse<OddsResponse>> getActiveOdds(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String[] sort) {
        
        Pageable pageable = buildPageable(page, size, sort);
        PageResponse<OddsResponse> response = service.getActiveOdds(pageable);
        return ResponseEntity.ok(response);
    }
    
    // GET /api/odds/{id} - Get odds by ID
    @GetMapping("/{id}")
    public ResponseEntity<OddsResponse> getOddsById(@PathVariable Long id) {
        OddsResponse odds = service.getOddsById(id);
        return ResponseEntity.ok(odds);
    }
    
    // GET /api/odds/sport/{sport} - Get odds by sport (supports pagination)
    @GetMapping("/sport/{sport}")
    public ResponseEntity<PageResponse<OddsResponse>> getOddsBySport(
            @PathVariable String sport,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String[] sort) {
        
        Pageable pageable = buildPageable(page, size, sort);
        PageResponse<OddsResponse> response = service.getOddsBySport(sport, pageable);
        return ResponseEntity.ok(response);
    }
    
    // GET /api/odds/upcoming - Get upcoming matches (supports pagination)
    @GetMapping("/upcoming")
    public ResponseEntity<PageResponse<OddsResponse>> getUpcomingMatches(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String[] sort) {
        
        Pageable pageable = buildPageable(page, size, sort);
        PageResponse<OddsResponse> response = service.getUpcomingMatches(pageable);
        return ResponseEntity.ok(response);
    }
    
    // GET /api/odds/team/{teamName} - Get matches for specific team (supports pagination)
    @GetMapping("/team/{teamName}")
    public ResponseEntity<PageResponse<OddsResponse>> getMatchesForTeam(
            @PathVariable String teamName,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String[] sort) {
        
        Pageable pageable = buildPageable(page, size, sort);
        PageResponse<OddsResponse> response = service.getMatchesForTeam(teamName, pageable);
        return ResponseEntity.ok(response);
    }
    
    // POST /api/odds - Create new odds
    @PostMapping
    public ResponseEntity<OddsResponse> createOdds(@Valid @RequestBody CreateOddsRequest request) {
        OddsResponse created = service.createOdds(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    // PUT /api/odds/{id} - Update existing odds
    @PutMapping("/{id}")
    public ResponseEntity<OddsResponse> updateOdds(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateOddsRequest request) {
        OddsResponse updated = service.updateOdds(id, request);
        return ResponseEntity.ok(updated);
    }
    
    // PATCH /api/odds/{id}/deactivate - Deactivate odds (soft delete)
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateOdds(@PathVariable Long id) {
        service.deactivateOdds(id);
        return ResponseEntity.ok(createSuccessResponse("Odds deactivated successfully"));
    }
    
    // DELETE /api/odds/{id} - Delete odds permanently
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteOdds(@PathVariable Long id) {
        service.deleteOdds(id);
        return ResponseEntity.ok(createSuccessResponse("Odds deleted successfully"));
    }
    
    // GET /api/odds/{id}/margin - Calculate bookmaker margin for specific odds
    @GetMapping("/{id}/margin")
    public ResponseEntity<OddsResponse> getBookmakerMargin(@PathVariable Long id) {
        // Use the new method that returns OddsResponse with margin calculated
        OddsResponse response = service.getOddsWithMargin(id);
        return ResponseEntity.ok(response);
    }
    
    // Helper method to create success response
    private Map<String, String> createSuccessResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return response;
    }
    
    // Helper method to build Pageable with sorting
    private Pageable buildPageable(Integer page, Integer size, String[] sort) {
        // If no pagination params, return unpaged (all results)
        if (page == null && size == null) {
            if (sort != null && sort.length > 0) {
                return Pageable.unpaged(buildSort(sort));
            }
            return Pageable.unpaged();
        }
        
        // Validate and set defaults
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 20; // Default 20 items per page
        if (pageSize > 100) pageSize = 100; // Max 100 items per page
        
        // Build sort
        Sort sortObj = (sort != null && sort.length > 0) 
                ? buildSort(sort) 
                : Sort.by(Sort.Direction.DESC, "id"); // Default: newest first
        
        return PageRequest.of(pageNumber, pageSize, sortObj);
    }
    
    // Helper method to build Sort from string array
    private Sort buildSort(String[] sort) {
        // Debug logging
        System.out.println("DEBUG: sort array length: " + sort.length);
        for (int i = 0; i < sort.length; i++) {
            System.out.println("DEBUG: sort[" + i + "] = '" + sort[i] + "'");
        }
        
        Sort.Order[] orders = new Sort.Order[sort.length];
        for (int i = 0; i < sort.length; i++) {
            String sortParam = sort[i];
            System.out.println("DEBUG: Processing sortParam: '" + sortParam + "'");
            
            String[] sortParams = sortParam.split(",");
            System.out.println("DEBUG: After split, length: " + sortParams.length);
            
            if (sortParams.length == 0) {
                throw new IllegalArgumentException("Invalid sort parameter: " + sortParam);
            }
            
            String property = sortParams[0].trim();
            System.out.println("DEBUG: Property: '" + property + "'");
            
            Sort.Direction direction = sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1].trim())
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            System.out.println("DEBUG: Direction: " + direction);
            
            orders[i] = new Sort.Order(direction, property);
        }
        return Sort.by(orders);
    }
}