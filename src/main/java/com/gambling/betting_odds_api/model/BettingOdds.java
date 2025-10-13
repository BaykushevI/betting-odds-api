package com.gambling.betting_odds_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "betting_odds")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class BettingOdds {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sport; // Football, Basketball, Tennis
    
    @Column(nullable = false)
    private String homeTeam;
    
    @Column(nullable = false)
    private String awayTeam;
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal homeOdds; // Decimal odds for home win
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal drawOdds; // Decimal odds for draw
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal awayOdds; // Decimal odds for away win
    
    @Column(nullable = false)
    private LocalDateTime matchDate;
    
    @Column(nullable = false)
    private Boolean active = true; // if odds is active
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
