-- V6: Account enhancements — sub-accounts, sorting, hiding, move transactions
-- Supports: F-AC1 (sub-accounts), F-AC2 (hide), F-AC3 (reorder), F-AC4 (delete with sub), F-AC5 (move all tx)

-- 1. Add parent_id for sub-account hierarchy
ALTER TABLE accounts ADD COLUMN parent_id BIGINT REFERENCES accounts(id) ON DELETE SET NULL;

-- 2. Add sort_order for drag-to-reorder
ALTER TABLE accounts ADD COLUMN sort_order INTEGER NOT NULL DEFAULT 0;

-- 3. Add hidden flag for hide/unhide accounts
ALTER TABLE accounts ADD COLUMN hidden BOOLEAN NOT NULL DEFAULT FALSE;

-- 4. Add account_id index to transactions for faster move-by-account queries
CREATE INDEX IF NOT EXISTS idx_transactions_account_id ON transactions(account_id);

COMMENT ON COLUMN accounts.parent_id IS 'Parent account ID for sub-accounts; NULL for top-level accounts';
COMMENT ON COLUMN accounts.sort_order IS 'Display order for drag-to-reorder';
COMMENT ON COLUMN accounts.hidden IS 'If true, account is hidden from UI';