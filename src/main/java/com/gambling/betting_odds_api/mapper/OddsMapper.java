package com.gambling.betting_odds_api.mapper;

import com.gambling.betting_odds_api.dto.CreateOddsRequest;
import com.gambling.betting_odds_api.dto.OddsResponse;
import com.gambling.betting_odds_api.dto.UpdateOddsRequest;
import com.gambling.betting_odds_api.model.BettingOdds;
import org.springframework.stereotype.Component;

// Mapper class to convert between Entity and DTOs
@Component
public class OddsMapper {

    // Convert CreateOddsRequest (DTO) → BettingOdds (Entity)
    public BettingOdds toEntity(CreateOddsRequest request) {
        BettingOdds odds = new BettingOdds();
        odds.setSport(request.getSport());
        odds.setHomeTeam(request.getHomeTeam());
        odds.setAwayTeam(request.getAwayTeam());
        odds.setHomeOdds(request.getHomeOdds());
        odds.setDrawOdds(request.getDrawOdds());
        odds.setAwayOdds(request.getAwayOdds());
        odds.setMatchDate(request.getMatchDate());
        odds.setActive(true); // Default to active for new odds
        return odds;
    }

    // Convert UpdateOddsRequest (DTO) → update Entity fields
    public void updateEntityFromDto(BettingOdds entity, UpdateOddsRequest request) {
        entity.setSport(request.getSport());
        entity.setHomeTeam(request.getHomeTeam());
        entity.setAwayTeam(request.getAwayTeam());
        entity.setHomeOdds(request.getHomeOdds());
        entity.setDrawOdds(request.getDrawOdds());
        entity.setAwayOdds(request.getAwayOdds());
        entity.setMatchDate(request.getMatchDate());
        
        if (request.getActive() != null) {
            entity.setActive(request.getActive());
        }
    }

    // Convert BettingOdds (Entity) → OddsResponse (DTO)
    public OddsResponse toResponse(BettingOdds entity) {
        return new OddsResponse(
                entity.getId(),
                entity.getSport(),
                entity.getHomeTeam(),
                entity.getAwayTeam(),
                entity.getHomeOdds(),
                entity.getDrawOdds(),
                entity.getAwayOdds(),
                entity.getMatchDate(),
                entity.getActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    // Convert BettingOdds (Entity) → OddsResponse with computed fields
    public OddsResponse toResponseWithMargin(BettingOdds entity) {
        OddsResponse response = toResponse(entity);
        
        // Calculate implied probabilities
        response.setImpliedProbabilityHome(1.0 / entity.getHomeOdds().doubleValue());
        response.setImpliedProbabilityDraw(1.0 / entity.getDrawOdds().doubleValue());
        response.setImpliedProbabilityAway(1.0 / entity.getAwayOdds().doubleValue());
        
        // Calculate bookmaker margin
        double totalProb = response.getImpliedProbabilityHome() 
                         + response.getImpliedProbabilityDraw() 
                         + response.getImpliedProbabilityAway();
        response.setBookmakerMargin((totalProb - 1.0) * 100);
        
        return response;
    }

}
