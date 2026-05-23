-- V3__categories.sql: Categories table

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    category_type VARCHAR(10) NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(id),
    parent_id BIGINT,
    sort_order INTEGER DEFAULT 0,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    created_by BIGINT,
    modified_by BIGINT
);

CREATE INDEX idx_categories_user_id ON categories(user_id);
CREATE INDEX idx_categories_type ON categories(category_type);

-- V4__transactions.sql: Transactions table

CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    transaction_type INTEGER NOT NULL,
    account_id BIGINT NOT NULL REFERENCES accounts(id),
    category_id BIGINT REFERENCES categories(id),
    amount BIGINT NOT NULL,
    description VARCHAR(255),
    transaction_time BIGINT NOT NULL,
    related_id BIGINT,
    user_id BIGINT NOT NULL REFERENCES users(id),
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    created_by BIGINT,
    modified_by BIGINT
);

CREATE INDEX idx_transactions_user_id ON transactions(user_id);
CREATE INDEX idx_transactions_account_id ON transactions(account_id);
CREATE INDEX idx_transactions_time ON transactions(transaction_time);
