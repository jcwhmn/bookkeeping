-- V10: Onboarding support
-- Adds onboarding completion tracking to users table

ALTER TABLE users ADD COLUMN IF NOT EXISTS onboarding_completed BOOLEAN DEFAULT FALSE;