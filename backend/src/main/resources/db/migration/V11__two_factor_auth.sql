-- V11__two_factor_auth.sql
-- Add Two-Factor Authentication fields

ALTER TABLE users ADD COLUMN totp_secret VARCHAR(64);
ALTER TABLE users ADD COLUMN totp_enabled BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN totp_created_at BIGINT;
ALTER TABLE users ADD COLUMN recovery_codes TEXT;