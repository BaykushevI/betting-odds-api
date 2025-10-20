package com.gambling.betting_odds_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request body for creating new betting odds")
public class CreateOddsRequest {
    
    @Schema(description = "Sport type (e.g., Football, Basketball)", example = "Football", required = true)
    @NotBlank(message = "Sport is required")
    @Size(min = 2, max = 50, message = "Sport must be between 2 and 50 characters")
    private String sport;
    
    @Schema(description = "Home team name", example = "Barcelona", required = true)
    @NotBlank(message = "Home team is required")
    @Size(min = 2, max = 100, message = "Home team must be between 2 and 100 characters")
    private String homeTeam;
    
    @Schema(description = "Away team name", example = "Real Madrid", required = true)
    @NotBlank(message = "Away team is required")
    @Size(min = 2, max = 100, message = "Away team must be between 2 and 100 characters")
    private String awayTeam;
    
    @Schema(description = "Descimal odds for home team win",
            example = "1.75",
            minimum = "1.01", 
            maximum = "999.99", 
            required = true)
    @NotNull(message = "Home odds are required")
    @DecimalMin(value = "1.01", message = "Home odds must be at least 1.01")
    @DecimalMax(value = "999.99", message = "Home odds cannot exceed 999.99")
    private BigDecimal homeOdds;
    
    @Schema(description = "Decimal odds for draw",
            example = "3.40",
            minimum = "1.01", 
            maximum = "999.99", 
            required = true)
    @NotNull(message = "Draw odds are required")
    @DecimalMin(value = "1.01", message = "Draw odds must be at least 1.01")
    @DecimalMax(value = "999.99", message = "Draw odds cannot exceed 999.99")
    private BigDecimal drawOdds;
    
    @Schema(description = "Decimal odds for away team win",
            example = "4.20",
            minimum = "1.01", 
            maximum = "999.99", 
            required = true)
    @NotNull(message = "Away odds are required")
    @DecimalMin(value = "1.01", message = "Away odds must be at least 1.01")
    @DecimalMax(value = "999.99", message = "Away odds cannot exceed 999.99")
    private BigDecimal awayOdds;
    
    @Schema(description = "Match date and time (ISO 8601 format)",
            example = "2024-12-31T20:00:00",
            required = true,
            type = "string",
            format = "date-time")
    @NotNull(message = "Match date is required")
    @Future(message = "Match date must be in the future")
    private LocalDateTime matchDate;
    
    // Note: We don't accept 'active', 'createdAt', 'updatedAt' from client
    // These are managed by the system
}