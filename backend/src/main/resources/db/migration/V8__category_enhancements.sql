-- V8: Category enhancements — hide, sort order, batch create
-- Supports: F-C3 (hide/unhide), F-C4 (reorder), F-C2 (batch create)

ALTER TABLE categories ADD COLUMN IF NOT EXISTS hidden BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX IF NOT EXISTS idx_categories_sort_order ON categories(user_id, sort_order);