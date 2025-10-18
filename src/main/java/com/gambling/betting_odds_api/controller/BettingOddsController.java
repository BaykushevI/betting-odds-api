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
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

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
            @RequestParam(required = false) List<String> sort) {
        
        Pageable pageable = buildPageable(page, size, sort);
        PageResponse<OddsResponse> response = service.getAllOdds(pageable);
        return ResponseEntity.ok(response);
    }
    
    // GET /api/odds/active - Get only active odds (supports pagination)
    @GetMapping("/active")
    public ResponseEntity<PageResponse<OddsResponse>> getActiveOdds(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) List<String> sort) {
        
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
            @RequestParam(required = false) List<String> sort) {
        
        Pageable pageable = buildPageable(page, size, sort);
        PageResponse<OddsResponse> response = service.getOddsBySport(sport, pageable);
        return ResponseEntity.ok(response);
    }
    
    // GET /api/odds/upcoming - Get upcoming matches (supports pagination)
    @GetMapping("/upcoming")
    public ResponseEntity<PageResponse<OddsResponse>> getUpcomingMatches(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) List<String> sort) {
        
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
            @RequestParam(required = false) List<String> sort) {
        
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
    private Pageable buildPageable(Integer page, Integer size, List<String> sort) {
        // If no pagination params, return unpaged (all results)
        if (page == null && size == null) {
            if (sort != null && !sort.isEmpty()) {
                return Pageable.unpaged(buildSort(sort));
            }
            return Pageable.unpaged();
        }
        
        // Validate and set defaults
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 20; // Default 20 items per page
        if (pageSize > 100) pageSize = 100; // Max 100 items per page
        
        // Build sort
        Sort sortObj = (sort != null && !sort.isEmpty()) 
                ? buildSort(sort) 
                : Sort.by(Sort.Direction.DESC, "id"); // Default: newest first
        
        return PageRequest.of(pageNumber, pageSize, sortObj);
    }
    
    // Helper method to build Sort from List of strings
    private Sort buildSort(List<String> sort) {
        // Debug logging
        System.out.println("DEBUG: sort list size: " + sort.size());
        for (int i = 0; i < sort.size(); i++) {
            System.out.println("DEBUG: sort[" + i + "] = '" + sort.get(i) + "'");
        }
        
        // First, we need to flatten the list by splitting each element by comma
        // Input: ["sport,asc", "homeOdds,desc"]
        // After flatten: ["sport", "asc", "homeOdds", "desc"]
        List<String> flattened = new ArrayList<>();
        for (String sortParam : sort) {
            String[] parts = sortParam.split(",");
            for (String part : parts) {
                flattened.add(part.trim());
            }
        }
        
        System.out.println("DEBUG: After flattening, size: " + flattened.size());
        for (int i = 0; i < flattened.size(); i++) {
            System.out.println("DEBUG: flattened[" + i + "] = '" + flattened.get(i) + "'");
        }
        
        // Now check if we have even number of elements (property, direction pairs)
        if (flattened.size() % 2 != 0) {
            throw new IllegalArgumentException("Sort parameters must come in pairs (property, direction). Got " + flattened.size() + " parameters after splitting.");
        }
        
        // Process pairs: [property, direction, property, direction, ...]
        int pairCount = flattened.size() / 2;
        Sort.Order[] orders = new Sort.Order[pairCount];
        
        for (int i = 0; i < flattened.size(); i += 2) {
            String property = flattened.get(i);
            String directionStr = flattened.get(i + 1);
            
            System.out.println("DEBUG: Pair " + (i/2) + " - Property: '" + property + "', Direction: '" + directionStr + "'");
            
            // Parse direction
            Sort.Direction direction;
            if ("desc".equalsIgnoreCase(directionStr)) {
                direction = Sort.Direction.DESC;
            } else if ("asc".equalsIgnoreCase(directionStr)) {
                direction = Sort.Direction.ASC;
            } else {
                throw new IllegalArgumentException("Invalid sort direction: '" + directionStr + "'. Must be 'asc' or 'desc'.");
            }
            
            System.out.println("DEBUG: Creating Sort.Order: property='" + property + "', direction=" + direction);
            orders[i / 2] = new Sort.Order(direction, property);
        }
        
        return Sort.by(orders);
    }
}