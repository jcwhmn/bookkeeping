-- V5__budgets.sql
-- Budgets table for monthly spending limits

CREATE TABLE IF NOT EXISTS budgets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    amount BIGINT NOT NULL,
    year INT NOT NULL,
    month INT NOT NULL,
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT
);

CREATE INDEX idx_budgets_user_month ON budgets(user_id, year, month);
CREATE INDEX idx_budgets_category ON budgets(category_id);

COMMENT ON TABLE budgets IS 'Monthly budget limits by category';