package com.gambling.betting_odds_api.service;

import com.gambling.betting_odds_api.model.BettingOdds;
import com.gambling.betting_odds_api.repository.BettingOddsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BettingOddsService {

    private final BettingOddsRepository repository;

    // CREATE - create new betting odds
    @Transactional
    public BettingOdds createOdds(BettingOdds odds){
        validateOdds(odds);

        if(odds.getActive() == null){
            odds.setActive(true);
        }

        return repository.save(odds);
    }

    //READ - get all odds
    public List<BettingOdds> getallOdds(){
        return repository.findAll();
    }

    //READ - get only active odds
    public List<BettingOdds> getActiveOdds(){
        return repository.findAll();
    }

    //READ - get odds by ID
    public Optional<BettingOdds> getOddsById(Long id){
        return repository.findById(id);
    }

    //READ - get odds by sport
    public List<BettingOdds> getOddsBySport(String sport){
        return repository.findBySportAndActiveTrue(sport);
    }

    //READ - get upgcoming matches
    public List<BettingOdds> getUpcomingMatches(){
        return repository.findUpcomingMatches(LocalDateTime.now());
    }

    //READ - get matches by team
    public List<BettingOdds> getMatchesForTeam(String teamName){
        return repository.findByTeam(teamName);
    }

    //UPDATE - update odds
    @Transactional
    public BettingOdds updateOdds(Long id, BettingOdds updatedOdds){
        //Check if there is matching odd
        BettingOdds existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Odds not found with id: " + id));
        

        validateOdds(updatedOdds);

        //update fields
        existing.setSport(updatedOdds.getSport());
        existing.setHomeTeam(updatedOdds.getHomeTeam());
        existing.setAwayTeam(updatedOdds.getAwayTeam());
        existing.setHomeOdds(updatedOdds.getHomeOdds());
        existing.setDrawOdds(updatedOdds.getDrawOdds());
        existing.setAwayOdds(updatedOdds.getAwayOdds());
        existing.setMatchDate(updatedOdds.getMatchDate());
        
        if (updatedOdds.getActive() != null) {
            existing.setActive(updatedOdds.getActive());
        }

        // @PreUpdate will call automatically and update UpdatedAt
        return repository.save(existing);
    }

    //Update - Deactivate odds
    @Transactional
    public void deactivateOdds(Long id){
        BettingOdds odds = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Odds not found with id: " + id));

        odds.setActive(false);
        repository.save(odds);
    }

    //DELETE - delete odds
    @Transactional
    public void deleteOdds(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Odds not found with id: " + id);
        }
        repository.deleteById(id);
    }

    //BUSINESS LOGIC - Calculate impliend probability from decimal odds
    public double calculateImpliedProbability(BigDecimal odds) {
        //Implied Probability = 1 / decimal odds
        //Example 2.50 odds = 1 / 2.50 = 0.40 = 40% probability
        return 1.0 / odds.doubleValue();
    }

    //BUSINESS LOGIC - Cauculate margin (overround) per bookie
    public double calculateBookmakerMargin(BettingOdds odds) {
        //Margin = (1 / homeOdds + 1 / drawOdds + 1 / awayOdds -1) * 100
        double homeProb = calculateImpliedProbability(odds.getHomeOdds());
        double drawProb = calculateImpliedProbability(odds.getDrawOdds());
        double awayProb = calculateImpliedProbability(odds.getAwayOdds());
        
        double totalProb = homeProb + drawProb + awayProb;
        return (totalProb - 1.0) * 100; // return as percentage
    }

    // VALIDATION - validation of odds
    private void validateOdds(BettingOdds odds){
        //Check if every mandatory fields are filled
        if (odds.getSport() == null || odds.getSport().trim().isEmpty()){
            throw new IllegalArgumentException("Sport cannot be empty");
        }
        if (odds.getHomeTeam() == null || odds.getHomeTeam().trim().isEmpty()) {
            throw new IllegalArgumentException("Home team cannot be empty");
        }
        
        if (odds.getAwayTeam() == null || odds.getAwayTeam().trim().isEmpty()) {
            throw new IllegalArgumentException("Away team cannot be empty");
        }
        
        // Chech if odds are valid (>= 1.01)
        validateOddsValue(odds.getHomeOdds(), "Home odds");
        validateOddsValue(odds.getDrawOdds(), "Draw odds");
        validateOddsValue(odds.getAwayOdds(), "Away odds");
        
        // Check that match date is in the future
        if (odds.getMatchDate() != null && odds.getMatchDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Match date must be in the future");
        }
    }
    
    private void validateOddsValue(BigDecimal oddsValue, String fieldName) {
        if (oddsValue == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
        
        BigDecimal minOdds = new BigDecimal("1.01");
        if (oddsValue.compareTo(minOdds) < 0) {
            throw new IllegalArgumentException(fieldName + " must be at least 1.01");
        }
    }
}
