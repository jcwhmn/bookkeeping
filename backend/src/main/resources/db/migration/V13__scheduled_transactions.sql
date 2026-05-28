-- V13: Scheduled Transactions (Recurring)
-- For automated recurring transactions

CREATE TABLE scheduled_transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    
    -- Transaction template
    transaction_type INT NOT NULL,
    account_id BIGINT NOT NULL,
    category_id BIGINT,
    destination_account_id BIGINT,
    amount BIGINT NOT NULL,
    description VARCHAR(500),
    tag_ids VARCHAR(500),
    
    -- Schedule
    frequency VARCHAR(20) NOT NULL,  -- 'daily', 'weekly', 'monthly', 'yearly'
    interval_days INT DEFAULT 1,       -- every N days/weeks/months
    day_of_week INT,                  -- 0=Sunday, 1=Monday... (for weekly)
    day_of_month INT,                 -- 1-31 (for monthly)
    month_of_year INT,                -- 1-12 (for yearly)
    
    -- Timing
    start_date BIGINT NOT NULL,       -- first occurrence
    end_date BIGINT,                  -- optional end date
    next_run_time BIGINT NOT NULL,    -- next execution timestamp
    
    -- Status
    active BOOLEAN NOT NULL DEFAULT TRUE,
    last_run_time BIGINT,
    last_run_result VARCHAR(50),     -- 'success', 'failed'
    run_count INT DEFAULT 0,
    
    -- Metadata
    created_at BIGINT NOT NULL DEFAULT EXTRACT(EPOCH FROM NOW())::BIGINT,
    updated_at BIGINT NOT NULL DEFAULT EXTRACT(EPOCH FROM NOW())::BIGINT,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at BIGINT
);

CREATE INDEX idx_scheduled_user ON scheduled_transactions(user_id);
CREATE INDEX idx_scheduled_next_run ON scheduled_transactions(next_run_time) WHERE active = TRUE;
CREATE INDEX idx_scheduled_active ON scheduled_transactions(user_id, active) WHERE active = TRUE;

-- Log of executed scheduled transactions
CREATE TABLE scheduled_transaction_logs (
    id BIGSERIAL PRIMARY KEY,
    scheduled_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    transaction_id BIGINT,            -- created transaction ID
    run_time BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,      -- 'success', 'failed', 'skipped'
    error_message VARCHAR(500),
    created_at BIGINT NOT NULL DEFAULT EXTRACT(EPOCH FROM NOW())::BIGINT
);

CREATE INDEX idx_logs_scheduled ON scheduled_transaction_logs(scheduled_id);
CREATE INDEX idx_logs_user ON scheduled_transaction_logs(user_id);
CREATE INDEX idx_logs_time ON scheduled_transaction_logs(run_time);

COMMENT ON TABLE scheduled_transactions IS 'Recurring/scheduled transactions';
COMMENT ON COLUMN scheduled_transactions.frequency IS 'daily, weekly, monthly, yearly';
COMMENT ON COLUMN scheduled_transactions.day_of_week IS '0=Sunday, 1=Monday...6=Saturday';
COMMENT ON COLUMN scheduled_transactions.day_of_month IS '1-31, -1=last day of month';