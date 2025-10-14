package com.gambling.betting_odds_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// DTO for returning betting odds in API responses
// This is what the client receives
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OddsResponse {
    
    private Long id;
    private String sport;
    private String homeTeam;
    private String awayTeam;
    private BigDecimal homeOdds;
    private BigDecimal drawOdds;
    private BigDecimal awayOdds;
    private LocalDateTime matchDate;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // We can add computed fields that don't exist in Entity
    private Double impliedProbabilityHome;
    private Double impliedProbabilityDraw;
    private Double impliedProbabilityAway;
    private Double bookmakerMargin;
    
    // Constructor without computed fields (for basic mapping)
    public OddsResponse(Long id, String sport, String homeTeam, String awayTeam,
                       BigDecimal homeOdds, BigDecimal drawOdds, BigDecimal awayOdds,
                       LocalDateTime matchDate, Boolean active,
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.sport = sport;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeOdds = homeOdds;
        this.drawOdds = drawOdds;
        this.awayOdds = awayOdds;
        this.matchDate = matchDate;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}