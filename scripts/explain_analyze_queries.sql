-- ===================================================================
-- Query Performance Analysis with EXPLAIN ANALYZE
-- ===================================================================
-- Purpose: Analyze query execution plans and identify performance issues
-- Database: betting_test
-- Author: Iliyan Baykushev
-- Date: 2025-01-XX (Phase 4 Week 2 Day 7)
-- ===================================================================

-- Enable timing for accurate measurements
\timing on

-- ===================================================================
-- BASELINE: Check current indexes
-- ===================================================================
SELECT 
    schemaname,
    tablename,
    indexname,
    indexdef
FROM pg_indexes
WHERE tablename = 'betting_odds'
ORDER BY indexname;

-- Check table statistics
SELECT 
    schemaname,
    relname,
    n_live_tup as row_count,
    n_dead_tup as dead_rows,
    last_vacuum,
    last_autovacuum,
    last_analyze,
    last_autoanalyze
FROM pg_stat_user_tables
WHERE relname = 'betting_odds';


-- ===================================================================
-- QUERY 1: findByActiveTrue (Simple index scan)
-- ===================================================================
-- Expected: Index scan on idx_sport_active or seq scan (small table)
-- Priority: MEDIUM (frequently used)
-- ===================================================================

EXPLAIN ANALYZE
SELECT * FROM betting_odds
WHERE active = true;

-- With pagination (LIMIT/OFFSET)
EXPLAIN ANALYZE
SELECT * FROM betting_odds
WHERE active = true
ORDER BY id
LIMIT 20 OFFSET 0;

-- ===================================================================
-- QUERY 2: findBySport (Sport filter)
-- ===================================================================
-- Expected: Index scan on idx_sport_active
-- Priority: HIGH (very frequently used)
-- ===================================================================

EXPLAIN ANALYZE
SELECT * FROM betting_odds
WHERE sport = 'Football';

-- With active filter (composite index test)
EXPLAIN ANALYZE
SELECT * FROM betting_odds
WHERE sport = 'Football' AND active = true;

-- With pagination
EXPLAIN ANALYZE
SELECT * FROM betting_odds
WHERE sport = 'Football' AND active = true
ORDER BY match_date DESC
LIMIT 20 OFFSET 0;

-- ===================================================================
-- QUERY 3: findUpcomingMatches (Date range + active)
-- ===================================================================
-- Expected: Index scan on idx_match_date
-- Priority: HIGH (critical for betting)
-- ===================================================================

EXPLAIN ANALYZE
SELECT * FROM betting_odds
WHERE match_date > NOW() AND active = true
ORDER BY match_date ASC;

-- With pagination
EXPLAIN ANALYZE
SELECT * FROM betting_odds
WHERE match_date > NOW() AND active = true
ORDER BY match_date ASC
LIMIT 20 OFFSET 0;

-- ===================================================================
-- QUERY 4: findByTeam (OR condition - PROBLEMATIC!)
-- ===================================================================
-- Expected: POSSIBLE INDEX SCAN or SEQ SCAN (OR is inefficient)
-- Priority: HIGH (needs optimization)
-- Test with real team names from our data
-- ===================================================================

-- Test with popular team
EXPLAIN ANALYZE
SELECT * FROM betting_odds
WHERE (home_team = 'Barcelona' OR away_team = 'Barcelona') 
  AND active = true;

-- Test with less popular team
EXPLAIN ANALYZE
SELECT * FROM betting_odds
WHERE (home_team = 'Team 5' OR away_team = 'Team 5') 
  AND active = true;

-- Alternative approach: UNION (potentially faster)
EXPLAIN ANALYZE
SELECT * FROM betting_odds WHERE home_team = 'Barcelona' AND active = true
UNION
SELECT * FROM betting_odds WHERE away_team = 'Barcelona' AND active = true;

-- ===================================================================
-- QUERY 5: findByHomeTeamContainingIgnoreCase (LIKE query - PROBLEMATIC!)
-- ===================================================================
-- Expected: SEQ SCAN (LIKE '%text%' cannot use index)
-- Priority: MEDIUM (needs optimization if used frequently)
-- ===================================================================

-- LIKE with wildcard on both sides (worst case)
EXPLAIN ANALYZE
SELECT * FROM betting_odds
WHERE LOWER(home_team) LIKE '%united%';

-- LIKE with wildcard only on right side (can use index with proper setup)
EXPLAIN ANALYZE
SELECT * FROM betting_odds
WHERE LOWER(home_team) LIKE 'manchester%';

-- Exact match for comparison (should use index)
EXPLAIN ANALYZE
SELECT * FROM betting_odds
WHERE home_team = 'Manchester United';

-- ===================================================================
-- QUERY 6: findByMatchDateBetween (Date range)
-- ===================================================================
-- Expected: Index scan on idx_match_date
-- Priority: MEDIUM
-- ===================================================================

-- 7-day window
EXPLAIN ANALYZE
SELECT * FROM betting_odds
WHERE match_date BETWEEN NOW() AND NOW() + INTERVAL '7 days';

-- 30-day window
EXPLAIN ANALYZE
SELECT * FROM betting_odds
WHERE match_date BETWEEN NOW() AND NOW() + INTERVAL '30 days';

-- ===================================================================
-- QUERY 7: Pagination performance test
-- ===================================================================
-- Test: Does OFFSET performance degrade with large offsets?
-- ===================================================================

-- Page 1 (offset 0)
EXPLAIN ANALYZE
SELECT * FROM betting_odds
WHERE active = true
ORDER BY id
LIMIT 20 OFFSET 0;

-- Page 5 (offset 80)
EXPLAIN ANALYZE
SELECT * FROM betting_odds
WHERE active = true
ORDER BY id
LIMIT 20 OFFSET 80;

-- Page 10 (offset 180) - near end of dataset
EXPLAIN ANALYZE
SELECT * FROM betting_odds
WHERE active = true
ORDER BY id
LIMIT 20 OFFSET 180;

-- ===================================================================
-- QUERY 8: Complex query with multiple filters
-- ===================================================================
-- Realistic query: Sport + Active + Date range + Sorting
-- ===================================================================

EXPLAIN ANALYZE
SELECT * FROM betting_odds
WHERE sport = 'Football'
  AND active = true
  AND match_date > NOW()
ORDER BY match_date ASC, id ASC
LIMIT 20;

-- ===================================================================
-- QUERY 9: Count queries (aggregation)
-- ===================================================================
-- Often used for pagination metadata
-- ===================================================================

EXPLAIN ANALYZE
SELECT COUNT(*) FROM betting_odds WHERE active = true;

EXPLAIN ANALYZE
SELECT COUNT(*) FROM betting_odds WHERE sport = 'Football' AND active = true;

-- ===================================================================
-- QUERY 10: JOIN simulation (future N+1 problem prevention)
-- ===================================================================
-- Simulate what happens if we add User relationship
-- Note: We don't have users foreign key yet, this is for future planning
-- ===================================================================

-- This would be N+1 problem if we fetch odds and then user for each record:
-- SELECT * FROM betting_odds; -- Returns 200 rows
-- For each row: SELECT * FROM users WHERE id = odds.user_id; -- 200 queries!

-- Better approach with JOIN FETCH (when we add relationship):
-- EXPLAIN ANALYZE
-- SELECT b.* FROM betting_odds b
-- LEFT JOIN users u ON b.user_id = u.id
-- WHERE b.active = true;

-- ===================================================================
-- SUMMARY QUERIES
-- ===================================================================

-- 1. Check which indexes are actually being used
SELECT 
    schemaname,
	relname,
    indexrelname as index_name,
    idx_scan as index_scans,
    idx_tup_read as tuples_read,
    idx_tup_fetch as tuples_fetched
FROM pg_stat_user_indexes
WHERE relname = 'betting_odds'
ORDER BY idx_scan DESC;

-- 2. Check table bloat and statistics
SELECT 
    tablename,
    attname as column_name,
    n_distinct as distinct_values,
    correlation
FROM pg_stats
WHERE tablename = 'betting_odds'
  AND attname IN ('sport', 'active', 'match_date', 'home_team', 'away_team')
ORDER BY attname;

-- 3. Analyze cache hit ratio (should be > 99% in production)
SELECT 
    sum(heap_blks_read) as heap_read,
    sum(heap_blks_hit) as heap_hit,
    sum(heap_blks_hit) / (sum(heap_blks_hit) + sum(heap_blks_read)) * 100 as cache_hit_ratio
FROM pg_statio_user_tables
WHERE relname = 'betting_odds';

-- ===================================================================
-- RECOMMENDATIONS TEMPLATE
-- ===================================================================
-- Based on EXPLAIN ANALYZE results, document:
--
-- 1. Queries with SEQ SCAN (full table scan) - need indexes
-- 2. Queries with high execution time (> 10ms)
-- 3. Indexes that are never used (idx_scan = 0) - consider removing
-- 4. Missing indexes for frequently filtered columns
-- 5. OR conditions that need UNION optimization
-- 6. LIKE queries that need full-text search indexes
-- ===================================================================