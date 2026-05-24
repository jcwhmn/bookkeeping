-- V7: Tag enhancements — tag groups, sort order, hide/unhide
-- Supports: F-TG1 (tag groups), F-TG3 (hide/unhide), F-TG4 (reorder)

-- 1. Add sort_order and hidden to tags table
ALTER TABLE tags ADD COLUMN IF NOT EXISTS sort_order INTEGER NOT NULL DEFAULT 0;
ALTER TABLE tags ADD COLUMN IF NOT EXISTS hidden BOOLEAN NOT NULL DEFAULT FALSE;

-- 2. Create tag_groups table
CREATE TABLE IF NOT EXISTS tag_groups (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    color VARCHAR(7) DEFAULT '#607D8B',
    sort_order INTEGER NOT NULL DEFAULT 0,
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_tag_groups_user_id ON tag_groups(user_id);
CREATE INDEX IF NOT EXISTS idx_tag_groups_sort_order ON tag_groups(user_id, sort_order);

COMMENT ON TABLE tag_groups IS 'Groups for organizing tags into categories';
COMMENT ON COLUMN tags.sort_order IS 'Display order for drag-to-reorder';
COMMENT ON COLUMN tags.hidden IS 'If true, tag is hidden from UI';
COMMENT ON COLUMN tag_groups.sort_order IS 'Display order for drag-to-reorder';