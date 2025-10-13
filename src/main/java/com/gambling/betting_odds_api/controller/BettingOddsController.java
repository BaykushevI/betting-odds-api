package com.gambling.betting_odds_api.controller;

import com.gambling.betting_odds_api.model.BettingOdds;
import com.gambling.betting_odds_api.service.BettingOddsService;
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

    //GET /api/odds - Get all odds
    @GetMapping
    public ResponseEntity<List<BettingOdds>> getAllOdds() {
        List<BettingOdds> odds = service.getallOdds();
        return ResponseEntity.ok(odds);
    }

    //GET /api/odds/active - Get only active odds
    @GetMapping("/active")
    public ResponseEntity<List<BettingOdds>> getActiveOdds() {
        List<BettingOdds> odds = service.getActiveOdds();
        return ResponseEntity.ok(odds);
    }
    
    // GET /api/odds/{id} - Get odds by ID
    @GetMapping("/{id}")
    public ResponseEntity<BettingOdds> getOddsById(@PathVariable Long id) {
        return service.getOddsById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // GET /api/odds/sport/{sport} - Get odds by sport
    @GetMapping("/sport/{sport}")
    public ResponseEntity<List<BettingOdds>> getOddsBySport(@PathVariable String sport) {
        List<BettingOdds> odds = service.getOddsBySport(sport);
        return ResponseEntity.ok(odds);
    }
    
    // GET /api/odds/upcoming - Get upcoming matches
    @GetMapping("/upcoming")
    public ResponseEntity<List<BettingOdds>> getUpcomingMatches() {
        List<BettingOdds> odds = service.getUpcomingMatches();
        return ResponseEntity.ok(odds);
    }
    
    // GET /api/odds/team/{teamName} - Get matches for specific team
    @GetMapping("/team/{teamName}")
    public ResponseEntity<List<BettingOdds>> getMatchesForTeam(@PathVariable String teamName) {
        List<BettingOdds> odds = service.getMatchesForTeam(teamName);
        return ResponseEntity.ok(odds);
    }

    //POST /api/odds - Create new odds
    @PostMapping
    public ResponseEntity<?> createOdds(@RequestBody BettingOdds odds) {
        try {
            BettingOdds created = service.createOdds(odds);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    //PUT /api/odds/{id} - Update existing odds
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOdds(@PathVariable Long id, @RequestBody BettingOdds odds) {
        try {
            BettingOdds updated = service.updateOdds(id, odds);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    //PATCH /api/odds/{id}/deactivate - Deactivate odds
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateOdds(@PathVariable Long id){
        try{
            service.deactivateOdds(id);
            return ResponseEntity.ok(createSuccessResponse("Odds deactivated successfully"));
        } catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    //DELETE /api/odds/{id} - Delete odds permanently
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOdds(@PathVariable Long id){
        try{
            service.deleteOdds(id);
            return ResponseEntity.ok(createSuccessResponse("Odds deleted successfully"));
        } catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/odds/{id}/margin - Calculate bookmaker margin for specific odds
    @GetMapping("/{id}/margin")
    public ResponseEntity<?> getBookmakerMargin(@PathVariable Long id) {
        return service.getOddsById(id)
                .map(odds -> {
                    double margin = service.calculateBookmakerMargin(odds);
                    Map<String, Object> response = new HashMap<>();
                    response.put("oddsId", id);
                    response.put("homeTeam", odds.getHomeTeam());
                    response.put("awayTeam", odds.getAwayTeam());
                    response.put("marginPercentage", String.format("%.2f%%", margin));
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }  

    //Helper method to create error response
    private Map<String, String> createErrorResponse(String message){
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }

    //Helper method to create success response
    private Map<String, String> createSuccessResponse(String message){
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return response;
    }
}
