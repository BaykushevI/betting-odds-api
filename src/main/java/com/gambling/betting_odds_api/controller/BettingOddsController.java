package com.gambling.betting_odds_api.controller;

import com.gambling.betting_odds_api.dto.CreateOddsRequest;
import com.gambling.betting_odds_api.dto.OddsResponse;
import com.gambling.betting_odds_api.dto.UpdateOddsRequest;
import com.gambling.betting_odds_api.service.BettingOddsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/odds")
@RequiredArgsConstructor
public class BettingOddsController {
    
    private final BettingOddsService service;
    
    // GET /api/odds - Get all odds
    @GetMapping
    public ResponseEntity<List<OddsResponse>> getAllOdds() {
        List<OddsResponse> odds = service.getAllOdds();
        return ResponseEntity.ok(odds);
    }
    
    // GET /api/odds/active - Get only active odds
    @GetMapping("/active")
    public ResponseEntity<List<OddsResponse>> getActiveOdds() {
        List<OddsResponse> odds = service.getActiveOdds();
        return ResponseEntity.ok(odds);
    }
    
    // GET /api/odds/{id} - Get odds by ID
    @GetMapping("/{id}")
    public ResponseEntity<OddsResponse> getOddsById(@PathVariable Long id) {
        OddsResponse odds = service.getOddsById(id);
        return ResponseEntity.ok(odds);
    }
    
    // GET /api/odds/sport/{sport} - Get odds by sport
    @GetMapping("/sport/{sport}")
    public ResponseEntity<List<OddsResponse>> getOddsBySport(@PathVariable String sport) {
        List<OddsResponse> odds = service.getOddsBySport(sport);
        return ResponseEntity.ok(odds);
    }
    
    // GET /api/odds/upcoming - Get upcoming matches
    @GetMapping("/upcoming")
    public ResponseEntity<List<OddsResponse>> getUpcomingMatches() {
        List<OddsResponse> odds = service.getUpcomingMatches();
        return ResponseEntity.ok(odds);
    }
    
    // GET /api/odds/team/{teamName} - Get matches for specific team
    @GetMapping("/team/{teamName}")
    public ResponseEntity<List<OddsResponse>> getMatchesForTeam(@PathVariable String teamName) {
        List<OddsResponse> odds = service.getMatchesForTeam(teamName);
        return ResponseEntity.ok(odds);
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
}