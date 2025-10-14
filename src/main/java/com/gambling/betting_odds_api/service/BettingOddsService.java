package com.gambling.betting_odds_api.service;

import com.gambling.betting_odds_api.dto.CreateOddsRequest;
import com.gambling.betting_odds_api.dto.OddsResponse;
import com.gambling.betting_odds_api.dto.UpdateOddsRequest;
import com.gambling.betting_odds_api.exception.ResourceNotFoundException;
import com.gambling.betting_odds_api.mapper.OddsMapper;
import com.gambling.betting_odds_api.model.BettingOdds;
import com.gambling.betting_odds_api.repository.BettingOddsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BettingOddsService {
    
    private final BettingOddsRepository repository;
    private final OddsMapper mapper;
    
    // CREATE - Create new betting odds
    @Transactional
    public OddsResponse createOdds(CreateOddsRequest request) {
        // Convert DTO to Entity
        BettingOdds odds = mapper.toEntity(request);
        
        // Save to database
        BettingOdds saved = repository.save(odds);
        
        // Convert Entity back to Response DTO
        return mapper.toResponse(saved);
    }
    
    // READ - Get all odds
    public List<OddsResponse> getAllOdds() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
    
    // READ - Get only active odds
    public List<OddsResponse> getActiveOdds() {
        return repository.findByActiveTrue().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
    
    // READ - Get odds by ID
    public OddsResponse getOddsById(Long id) {
        BettingOdds odds = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Betting Odds", id));
        return mapper.toResponse(odds);
    }
    
    // READ - Get odds by sport
    public List<OddsResponse> getOddsBySport(String sport) {
        return repository.findBySportAndActiveTrue(sport).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
    
    // READ - Get upcoming matches
    public List<OddsResponse> getUpcomingMatches() {
        return repository.findUpcomingMatches(java.time.LocalDateTime.now()).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
    
    // READ - Get matches for specific team
    public List<OddsResponse> getMatchesForTeam(String teamName) {
        return repository.findByTeam(teamName).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
    
    // UPDATE - Update existing odds
    @Transactional
    public OddsResponse updateOdds(Long id, UpdateOddsRequest request) {
        // Find existing entity
        BettingOdds existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Betting Odds", id));
        
        // Update entity from DTO
        mapper.updateEntityFromDto(existing, request);
        
        // Save and return response
        BettingOdds updated = repository.save(existing);
        return mapper.toResponse(updated);
    }
    
    // UPDATE - Deactivate odds (soft delete)
    @Transactional
    public void deactivateOdds(Long id) {
        BettingOdds odds = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Betting Odds", id));
        
        odds.setActive(false);
        repository.save(odds);
    }
    
    // DELETE - Delete odds permanently (hard delete)
    @Transactional
    public void deleteOdds(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Betting Odds", id);
        }
        repository.deleteById(id);
    }
    
    // BUSINESS LOGIC - Get odds with calculated margin
    public OddsResponse getOddsWithMargin(Long id) {
        BettingOdds odds = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Betting Odds", id));
        return mapper.toResponseWithMargin(odds);
    }
}