-- V4__tags.sql
-- Tags table for organizing transactions

CREATE TABLE IF NOT EXISTS tags (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    color VARCHAR(7) DEFAULT '#1976D2',
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX idx_tags_user_id ON tags(user_id);
CREATE INDEX idx_tags_name ON tags(name);

-- Add tag_ids column to transactions (stored as comma-separated IDs)
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS tag_ids TEXT;

COMMENT ON TABLE tags IS 'User-defined tags for categorizing transactions';
COMMENT ON COLUMN tags.color IS 'Hex color code for tag display';