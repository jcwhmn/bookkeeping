-- V14: Category enhancements - icon, color, comment fields

-- Add icon column (Material Design Icon identifier)
ALTER TABLE categories ADD COLUMN IF NOT EXISTS icon VARCHAR(64);

-- Add color column (6-char hex with # prefix)
ALTER TABLE categories ADD COLUMN IF NOT EXISTS color VARCHAR(7);

-- Add comment column
ALTER TABLE categories ADD COLUMN IF NOT EXISTS comment VARCHAR(255);

-- Add check constraint for color format
ALTER TABLE categories ADD CONSTRAINT chk_category_color_format 
    CHECK (color IS NULL OR color ~ '^#[0-9A-Fa-f]{6}$');
