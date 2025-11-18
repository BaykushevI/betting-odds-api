-- ===================================================================
-- Database Migration: Add created_by_user_id column
-- ===================================================================
-- Purpose: Add relationship between BettingOdds and User
-- Allows tracking which bookmaker/admin created each odds record
-- 
-- Phase: 4 (Performance & Reliability)
-- Week: 2 (Database Optimization)
-- Day: 9 (N+1 Problem Resolution)
-- ===================================================================

-- Step 1: Add new column (nullable for backward compatibility)
ALTER TABLE betting_odds 
ADD COLUMN created_by_user_id BIGINT;

-- Step 2: Add foreign key constraint
ALTER TABLE betting_odds 
ADD CONSTRAINT fk_betting_odds_user 
FOREIGN KEY (created_by_user_id) 
REFERENCES users(id) 
ON DELETE SET NULL;  -- If user is deleted, set to NULL (preserve odds data)

-- Step 3: Add index for performance (foreign key lookups)
CREATE INDEX idx_created_by_user_id ON betting_odds(created_by_user_id);

-- Step 4: (Optional) Populate with existing data
-- Assign all existing odds to first BOOKMAKER or ADMIN user
UPDATE betting_odds 
SET created_by_user_id = (
    SELECT id FROM users 
    WHERE role IN ('BOOKMAKER', 'ADMIN') 
    ORDER BY id 
    LIMIT 1
)
WHERE created_by_user_id IS NULL;

-- Verify migration
SELECT 
    COUNT(*) as total_odds,
    COUNT(created_by_user_id) as odds_with_creator,
    COUNT(*) - COUNT(created_by_user_id) as odds_without_creator
FROM betting_odds;

-- Expected result: All odds should have a creator after Step 4

-- ===================================================================
-- ROLLBACK SCRIPT (if needed)
-- ===================================================================
-- Run these commands to undo the migration:
--
-- ALTER TABLE betting_odds DROP CONSTRAINT fk_betting_odds_user;
-- DROP INDEX idx_created_by_user_id;
-- ALTER TABLE betting_odds DROP COLUMN created_by_user_id;
-- ===================================================================