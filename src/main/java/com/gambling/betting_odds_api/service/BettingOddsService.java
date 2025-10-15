package com.gambling.betting_odds_api.service;

import com.gambling.betting_odds_api.dto.CreateOddsRequest;
import com.gambling.betting_odds_api.dto.OddsResponse;
import com.gambling.betting_odds_api.dto.PageResponse;
import com.gambling.betting_odds_api.dto.UpdateOddsRequest;
import com.gambling.betting_odds_api.exception.ResourceNotFoundException;
import com.gambling.betting_odds_api.mapper.OddsMapper;
import com.gambling.betting_odds_api.model.BettingOdds;
import com.gambling.betting_odds_api.repository.BettingOddsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    
    // READ - Get all odds with optional pagination
    public PageResponse<OddsResponse> getAllOdds(Pageable pageable) {
        Page<BettingOdds> page = repository.findAll(pageable);
        Page<OddsResponse> responsePage = page.map(mapper::toResponse);
        return PageResponse.of(responsePage);
    }
    
    // READ - Get active odds with optional pagination
    public PageResponse<OddsResponse> getActiveOdds(Pageable pageable) {
        Page<BettingOdds> page = repository.findByActiveTrue(pageable);
        Page<OddsResponse> responsePage = page.map(mapper::toResponse);
        return PageResponse.of(responsePage);
    }
    
    // READ - Get odds by ID (single item - no pagination needed)
    public OddsResponse getOddsById(Long id) {
        BettingOdds odds = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Betting Odds", id));
        return mapper.toResponse(odds);
    }
    
    // READ - Get odds by sport with optional pagination
    public PageResponse<OddsResponse> getOddsBySport(String sport, Pageable pageable) {
        Page<BettingOdds> page = repository.findBySportAndActiveTrue(sport, pageable);
        Page<OddsResponse> responsePage = page.map(mapper::toResponse);
        return PageResponse.of(responsePage);
    }
    
    // READ - Get upcoming matches with optional pagination
    public PageResponse<OddsResponse> getUpcomingMatches(Pageable pageable) {
        Page<BettingOdds> page = repository.findUpcomingMatches(java.time.LocalDateTime.now(), pageable);
        Page<OddsResponse> responsePage = page.map(mapper::toResponse);
        return PageResponse.of(responsePage);
    }
    
    // READ - Get matches for specific team with optional pagination
    public PageResponse<OddsResponse> getMatchesForTeam(String teamName, Pageable pageable) {
        Page<BettingOdds> page = repository.findByTeam(teamName, pageable);
        Page<OddsResponse> responsePage = page.map(mapper::toResponse);
        return PageResponse.of(responsePage);
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