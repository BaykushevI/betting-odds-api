-- ===================================================================
-- Test Data Generation Script for Betting Odds API
-- ===================================================================
-- Purpose: Generate 200 realistic betting odds records for performance testing
-- Target: PostgreSQL 18
-- Database: betting_test
-- Author: Iliyan Baykushev
-- Date: 2025-01-XX (Phase 4 Week 2 Day 7)
-- ===================================================================

-- Clear existing data (optional - uncomment if needed)
-- TRUNCATE TABLE betting_odds RESTART IDENTITY CASCADE;

-- ===================================================================
-- FOOTBALL MATCHES (100 records)
-- ===================================================================
-- Popular European teams
INSERT INTO betting_odds (sport, home_team, away_team, home_odds, draw_odds, away_odds, match_date, active, created_at, updated_at)
VALUES
    -- Premier League matches
    ('Football', 'Manchester United', 'Liverpool', 2.10, 3.40, 3.60, NOW() + INTERVAL '7 days', true, NOW(), NOW()),
    ('Football', 'Arsenal', 'Chelsea', 2.30, 3.20, 3.40, NOW() + INTERVAL '8 days', true, NOW(), NOW()),
    ('Football', 'Manchester City', 'Tottenham', 1.80, 3.80, 4.50, NOW() + INTERVAL '9 days', true, NOW(), NOW()),
    ('Football', 'Newcastle', 'Aston Villa', 2.50, 3.30, 2.90, NOW() + INTERVAL '10 days', true, NOW(), NOW()),
    ('Football', 'West Ham', 'Brighton', 2.40, 3.25, 3.10, NOW() + INTERVAL '11 days', true, NOW(), NOW()),
    
    -- La Liga matches
    ('Football', 'Barcelona', 'Real Madrid', 2.20, 3.30, 3.50, NOW() + INTERVAL '12 days', true, NOW(), NOW()),
    ('Football', 'Atletico Madrid', 'Sevilla', 2.00, 3.40, 3.80, NOW() + INTERVAL '13 days', true, NOW(), NOW()),
    ('Football', 'Valencia', 'Real Betis', 2.60, 3.20, 2.80, NOW() + INTERVAL '14 days', true, NOW(), NOW()),
    ('Football', 'Villarreal', 'Real Sociedad', 2.45, 3.15, 3.00, NOW() + INTERVAL '15 days', true, NOW(), NOW()),
    ('Football', 'Athletic Bilbao', 'Getafe', 2.30, 3.10, 3.20, NOW() + INTERVAL '16 days', true, NOW(), NOW()),
    
    -- Serie A matches
    ('Football', 'Juventus', 'Inter Milan', 2.40, 3.20, 3.30, NOW() + INTERVAL '17 days', true, NOW(), NOW()),
    ('Football', 'AC Milan', 'Napoli', 2.50, 3.30, 2.90, NOW() + INTERVAL '18 days', true, NOW(), NOW()),
    ('Football', 'Roma', 'Lazio', 2.35, 3.15, 3.25, NOW() + INTERVAL '19 days', true, NOW(), NOW()),
    ('Football', 'Atalanta', 'Fiorentina', 2.20, 3.25, 3.50, NOW() + INTERVAL '20 days', true, NOW(), NOW()),
    ('Football', 'Torino', 'Bologna', 2.55, 3.20, 2.85, NOW() + INTERVAL '21 days', true, NOW(), NOW()),
    
    -- Bundesliga matches
    ('Football', 'Bayern Munich', 'Borussia Dortmund', 1.95, 3.60, 4.00, NOW() + INTERVAL '22 days', true, NOW(), NOW()),
    ('Football', 'RB Leipzig', 'Bayer Leverkusen', 2.25, 3.30, 3.40, NOW() + INTERVAL '23 days', true, NOW(), NOW()),
    ('Football', 'Eintracht Frankfurt', 'Wolfsburg', 2.40, 3.20, 3.10, NOW() + INTERVAL '24 days', true, NOW(), NOW()),
    ('Football', 'Borussia Monchengladbach', 'Union Berlin', 2.50, 3.15, 3.00, NOW() + INTERVAL '25 days', true, NOW(), NOW()),
    ('Football', 'Stuttgart', 'Hoffenheim', 2.35, 3.25, 3.15, NOW() + INTERVAL '26 days', true, NOW(), NOW()),
    
    -- Ligue 1 matches
    ('Football', 'Paris Saint-Germain', 'Marseille', 1.70, 4.00, 5.00, NOW() + INTERVAL '27 days', true, NOW(), NOW()),
    ('Football', 'Lyon', 'Monaco', 2.30, 3.20, 3.40, NOW() + INTERVAL '28 days', true, NOW(), NOW()),
    ('Football', 'Lille', 'Nice', 2.40, 3.15, 3.20, NOW() + INTERVAL '29 days', true, NOW(), NOW()),
    ('Football', 'Rennes', 'Lens', 2.50, 3.10, 3.00, NOW() + INTERVAL '30 days', true, NOW(), NOW()),
    ('Football', 'Strasbourg', 'Montpellier', 2.45, 3.20, 2.95, NOW() + INTERVAL '31 days', true, NOW(), NOW());

-- Generate 75 more random football matches
INSERT INTO betting_odds (sport, home_team, away_team, home_odds, draw_odds, away_odds, match_date, active, created_at, updated_at)
SELECT 
    'Football',
    'Team ' || (floor(random() * 25) + 1)::text,  -- 25 different home teams
    'Team ' || (floor(random() * 25) + 26)::text, -- 25 different away teams
    round((random() * 3 + 1.5)::numeric, 2), -- home_odds: 1.5 - 4.5
    round((random() * 2 + 2.5)::numeric, 2), -- draw_odds: 2.5 - 4.5
    round((random() * 3 + 1.5)::numeric, 2), -- away_odds: 1.5 - 4.5
    NOW() + (random() * interval '60 days'), -- Random dates in next 60 days
    random() > 0.2, -- 80% active, 20% inactive
    NOW(),
    NOW()
FROM generate_series(1, 75);

-- ===================================================================
-- BASKETBALL MATCHES (50 records)
-- ===================================================================
INSERT INTO betting_odds (sport, home_team, away_team, home_odds, draw_odds, away_odds, match_date, active, created_at, updated_at)
VALUES
    -- NBA matches (no draw in basketball, draw_odds = 1.00)
    ('Basketball', 'Los Angeles Lakers', 'Boston Celtics', 1.95, 1.00, 1.85, NOW() + INTERVAL '5 days', true, NOW(), NOW()),
    ('Basketball', 'Golden State Warriors', 'Phoenix Suns', 2.10, 1.00, 1.75, NOW() + INTERVAL '6 days', true, NOW(), NOW()),
    ('Basketball', 'Milwaukee Bucks', 'Brooklyn Nets', 1.80, 1.00, 2.05, NOW() + INTERVAL '7 days', true, NOW(), NOW()),
    ('Basketball', 'Philadelphia 76ers', 'Miami Heat', 2.00, 1.00, 1.85, NOW() + INTERVAL '8 days', true, NOW(), NOW()),
    ('Basketball', 'Denver Nuggets', 'Dallas Mavericks', 1.90, 1.00, 1.95, NOW() + INTERVAL '9 days', true, NOW(), NOW()),
    ('Basketball', 'Memphis Grizzlies', 'Sacramento Kings', 2.05, 1.00, 1.80, NOW() + INTERVAL '10 days', true, NOW(), NOW()),
    ('Basketball', 'Cleveland Cavaliers', 'New York Knicks', 2.15, 1.00, 1.72, NOW() + INTERVAL '11 days', true, NOW(), NOW()),
    ('Basketball', 'LA Clippers', 'Minnesota Timberwolves', 1.85, 1.00, 2.00, NOW() + INTERVAL '12 days', true, NOW(), NOW()),
    ('Basketball', 'Atlanta Hawks', 'Chicago Bulls', 2.10, 1.00, 1.75, NOW() + INTERVAL '13 days', true, NOW(), NOW()),
    ('Basketball', 'Toronto Raptors', 'Indiana Pacers', 2.05, 1.00, 1.80, NOW() + INTERVAL '14 days', true, NOW(), NOW()),
    
    -- EuroLeague matches
    ('Basketball', 'Real Madrid', 'Barcelona', 1.95, 1.00, 1.90, NOW() + INTERVAL '15 days', true, NOW(), NOW()),
    ('Basketball', 'Panathinaikos', 'Olympiacos', 2.00, 1.00, 1.85, NOW() + INTERVAL '16 days', true, NOW(), NOW()),
    ('Basketball', 'CSKA Moscow', 'Fenerbahce', 1.88, 1.00, 1.97, NOW() + INTERVAL '17 days', true, NOW(), NOW()),
    ('Basketball', 'Zalgiris Kaunas', 'Maccabi Tel Aviv', 2.10, 1.00, 1.75, NOW() + INTERVAL '18 days', true, NOW(), NOW()),
    ('Basketball', 'Bayern Munich', 'Anadolu Efes', 2.05, 1.00, 1.80, NOW() + INTERVAL '19 days', true, NOW(), NOW());

-- Generate 35 more random basketball matches
INSERT INTO betting_odds (sport, home_team, away_team, home_odds, draw_odds, away_odds, match_date, active, created_at, updated_at)
SELECT 
    'Basketball',
    'Team ' || (floor(random() * 20) + 1)::text,
    'Team ' || (floor(random() * 20) + 21)::text,
    round((random() * 2 + 1.3)::numeric, 2), -- home_odds: 1.3 - 3.3
    1.00, -- No draw in basketball
    round((random() * 2 + 1.3)::numeric, 2), -- away_odds: 1.3 - 3.3
    NOW() + (random() * interval '30 days'),
    random() > 0.15, -- 85% active
    NOW(),
    NOW()
FROM generate_series(1, 35);

-- ===================================================================
-- TENNIS MATCHES (50 records)
-- ===================================================================
INSERT INTO betting_odds (sport, home_team, away_team, home_odds, draw_odds, away_odds, match_date, active, created_at, updated_at)
VALUES
    -- Grand Slam players (no draw in tennis, draw_odds = 1.00)
    ('Tennis', 'Novak Djokovic', 'Carlos Alcaraz', 1.95, 1.00, 1.90, NOW() + INTERVAL '3 days', true, NOW(), NOW()),
    ('Tennis', 'Rafael Nadal', 'Daniil Medvedev', 2.10, 1.00, 1.75, NOW() + INTERVAL '4 days', true, NOW(), NOW()),
    ('Tennis', 'Stefanos Tsitsipas', 'Jannik Sinner', 2.05, 1.00, 1.80, NOW() + INTERVAL '5 days', true, NOW(), NOW()),
    ('Tennis', 'Alexander Zverev', 'Andrey Rublev', 1.88, 1.00, 1.97, NOW() + INTERVAL '6 days', true, NOW(), NOW()),
    ('Tennis', 'Casper Ruud', 'Holger Rune', 2.00, 1.00, 1.85, NOW() + INTERVAL '7 days', true, NOW(), NOW()),
    ('Tennis', 'Taylor Fritz', 'Frances Tiafoe', 1.92, 1.00, 1.93, NOW() + INTERVAL '8 days', true, NOW(), NOW()),
    ('Tennis', 'Tommy Paul', 'Ben Shelton', 2.05, 1.00, 1.80, NOW() + INTERVAL '9 days', true, NOW(), NOW()),
    ('Tennis', 'Grigor Dimitrov', 'Karen Khachanov', 1.95, 1.00, 1.90, NOW() + INTERVAL '10 days', true, NOW(), NOW()),
    ('Tennis', 'Hubert Hurkacz', 'Felix Auger-Aliassime', 2.00, 1.00, 1.85, NOW() + INTERVAL '11 days', true, NOW(), NOW()),
    ('Tennis', 'Cameron Norrie', 'Alex de Minaur', 2.10, 1.00, 1.75, NOW() + INTERVAL '12 days', true, NOW(), NOW()),
    
    -- WTA matches
    ('Tennis', 'Iga Swiatek', 'Aryna Sabalenka', 1.85, 1.00, 2.00, NOW() + INTERVAL '3 days', true, NOW(), NOW()),
    ('Tennis', 'Coco Gauff', 'Elena Rybakina', 2.05, 1.00, 1.80, NOW() + INTERVAL '4 days', true, NOW(), NOW()),
    ('Tennis', 'Jessica Pegula', 'Ons Jabeur', 1.95, 1.00, 1.90, NOW() + INTERVAL '5 days', true, NOW(), NOW()),
    ('Tennis', 'Caroline Garcia', 'Maria Sakkari', 2.00, 1.00, 1.85, NOW() + INTERVAL '6 days', true, NOW(), NOW()),
    ('Tennis', 'Petra Kvitova', 'Belinda Bencic', 2.10, 1.00, 1.75, NOW() + INTERVAL '7 days', true, NOW(), NOW());

-- Generate 35 more random tennis matches
INSERT INTO betting_odds (sport, home_team, away_team, home_odds, draw_odds, away_odds, match_date, active, created_at, updated_at)
SELECT 
    'Tennis',
    'Player ' || (floor(random() * 25) + 1)::text,
    'Player ' || (floor(random() * 25) + 26)::text,
    round((random() * 3 + 1.2)::numeric, 2), -- home_odds: 1.2 - 4.2
    1.00, -- No draw in tennis
    round((random() * 3 + 1.2)::numeric, 2), -- away_odds: 1.2 - 4.2
    NOW() + (random() * interval '14 days'),
    random() > 0.1, -- 90% active
    NOW(),
    NOW()
FROM generate_series(1, 35);

-- ===================================================================
-- SUMMARY
-- ===================================================================
-- Football: 100 records (25 real teams + 75 random)
-- Basketball: 50 records (15 real teams + 35 random)
-- Tennis: 50 records (15 real players + 35 random)
-- Total: 200 records
-- Active: ~80-85%
-- Date range: Next 60 days
-- ===================================================================

-- Verify data
SELECT 
    sport,
    COUNT(*) as total_matches,
    SUM(CASE WHEN active = true THEN 1 ELSE 0 END) as active_matches,
    SUM(CASE WHEN active = false THEN 1 ELSE 0 END) as inactive_matches,
    MIN(match_date) as earliest_match,
    MAX(match_date) as latest_match
FROM betting_odds
GROUP BY sport
ORDER BY sport;

-- Overall summary
SELECT 
    COUNT(*) as total_records,
    SUM(CASE WHEN active = true THEN 1 ELSE 0 END) as active_records,
    SUM(CASE WHEN active = false THEN 1 ELSE 0 END) as inactive_records,
    COUNT(DISTINCT sport) as different_sports,
    COUNT(DISTINCT home_team) as unique_home_teams,
    COUNT(DISTINCT away_team) as unique_away_teams
FROM betting_odds;