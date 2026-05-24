-- V9: User profile extension + data management
-- Supports: F-U1 (extended profile), F-D1 (export CSV/TSV), F-D2 (clear data), F-D4 (data statistics)

-- 1. Extend users table with profile fields
ALTER TABLE users ADD COLUMN IF NOT EXISTS avatar VARCHAR(500);
ALTER TABLE users ADD COLUMN IF NOT EXISTS week_start_day INTEGER;
ALTER TABLE users ADD COLUMN IF NOT EXISTS fy_start_month INTEGER;
ALTER TABLE users ADD COLUMN IF NOT EXISTS date_format_string VARCHAR(10);
ALTER TABLE users ADD COLUMN IF NOT EXISTS transaction_edit_scope INTEGER;