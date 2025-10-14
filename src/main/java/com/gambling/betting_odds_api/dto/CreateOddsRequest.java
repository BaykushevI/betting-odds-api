package com.gambling.betting_odds_api.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// DTO for creating new betting odds
// Client sends this in POST request body

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOddsRequest {

    @NotBlank(message = "Sport is required")
    @Size(min = 2, max = 50, message = "Sport must be between 2 and 50 characters")
    private String sport;

    @NotBlank(message = "Home team is required")
    @Size(min = 2, max = 100, message = "Home team must be between 2 and 100 characters")
    private String homeTeam;
    
    @NotBlank(message = "Away team is required")
    @Size(min = 2, max = 100, message = "Away team must be between 2 and 100 characters")
    private String awayTeam;
    
    @NotNull(message = "Home odds are required")
    @DecimalMin(value = "1.01", message = "Home odds must be at least 1.01")
    @DecimalMax(value = "999.99", message = "Home odds cannot exceed 999.99")
    private BigDecimal homeOdds;
    
    @NotNull(message = "Draw odds are required")
    @DecimalMin(value = "1.01", message = "Draw odds must be at least 1.01")
    @DecimalMax(value = "999.99", message = "Draw odds cannot exceed 999.99")
    private BigDecimal drawOdds;
    
    @NotNull(message = "Away odds are required")
    @DecimalMin(value = "1.01", message = "Away odds must be at least 1.01")
    @DecimalMax(value = "999.99", message = "Away odds cannot exceed 999.99")
    private BigDecimal awayOdds;
    
    @NotNull(message = "Match date is required")
    @Future(message = "Match date must be in the future")
    private LocalDateTime matchDate;
    
    // Note: We don't accept 'active', 'createdAt', 'updatedAt' from client
    // These are managed by the system

}
