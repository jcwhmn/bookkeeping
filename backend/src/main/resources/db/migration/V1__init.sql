-- V1__init.sql: Initial schema for bookkeeping application

-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(32) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    nickname VARCHAR(64),
    password VARCHAR(64) NOT NULL,
    salt VARCHAR(10) NOT NULL,
    default_currency VARCHAR(3) DEFAULT 'USD',
    default_account_id BIGINT,
    language VARCHAR(10) DEFAULT 'en-US',
    email_verified BOOLEAN DEFAULT FALSE,
    disabled BOOLEAN DEFAULT FALSE,
    deleted BOOLEAN DEFAULT FALSE,
    deleted_unix_time BIGINT,
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT NOT NULL,
    created_by BIGINT,
    modified_by BIGINT
);

-- Tokens table
CREATE TABLE tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    token VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL,
    user_agent TEXT,
    last_active_time BIGINT,
    expires_at BIGINT NOT NULL,
    created_unix_time BIGINT NOT NULL,
    created_by BIGINT,
    modified_by BIGINT,
    CONSTRAINT uk_tokens_token UNIQUE (token)
);

-- Accounts table
CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    balance BIGINT NOT NULL DEFAULT 0,
    icon VARCHAR(50),
    color VARCHAR(7),
    notes TEXT,
    include_in_total BOOLEAN DEFAULT TRUE,
    archived BOOLEAN DEFAULT FALSE,
    deleted BOOLEAN DEFAULT FALSE,
    deleted_unix_time BIGINT,
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT NOT NULL,
    created_by BIGINT,
    modified_by BIGINT,
    CONSTRAINT uk_accounts_name_user UNIQUE (name, user_id)
);

-- Categories table
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    parent_id BIGINT REFERENCES categories(id),
    name VARCHAR(100) NOT NULL,
    type VARCHAR(10) NOT NULL,
    icon VARCHAR(50),
    color VARCHAR(7),
    sort_order INT DEFAULT 0,
    deleted BOOLEAN DEFAULT FALSE,
    deleted_unix_time BIGINT,
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT NOT NULL,
    created_by BIGINT,
    modified_by BIGINT,
    CONSTRAINT uk_categories_name_user_type UNIQUE (name, user_id, type)
);

-- Tags table
CREATE TABLE tags (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    name VARCHAR(50) NOT NULL,
    color VARCHAR(7),
    icon VARCHAR(50),
    deleted BOOLEAN DEFAULT FALSE,
    deleted_unix_time BIGINT,
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT NOT NULL,
    created_by BIGINT,
    modified_by BIGINT,
    CONSTRAINT uk_tags_name_user UNIQUE (name, user_id)
);

-- Transactions table
CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    type VARCHAR(20) NOT NULL,
    amount BIGINT NOT NULL,
    currency VARCHAR(3) NOT NULL,
    account_id BIGINT NOT NULL REFERENCES accounts(id),
    destination_account_id BIGINT REFERENCES accounts(id),
    category_id BIGINT REFERENCES categories(id),
    related_transaction_id BIGINT REFERENCES transactions(id),
    transaction_time BIGINT NOT NULL,
    notes TEXT,
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT NOT NULL,
    created_by BIGINT,
    modified_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE,
    deleted_unix_time BIGINT
);

-- Transaction tags (N:M)
CREATE TABLE transaction_tags (
    transaction_id BIGINT NOT NULL REFERENCES transactions(id),
    tag_id BIGINT NOT NULL REFERENCES tags(id),
    PRIMARY KEY (transaction_id, tag_id)
);

-- Budgets table
CREATE TABLE budgets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    category_id BIGINT NOT NULL REFERENCES categories(id),
    amount BIGINT NOT NULL,
    period VARCHAR(20) NOT NULL,
    custom_period_start INT,
    rollover BOOLEAN DEFAULT FALSE,
    alert_threshold INT DEFAULT 80,
    enabled BOOLEAN DEFAULT TRUE,
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT NOT NULL,
    created_by BIGINT,
    modified_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE,
    deleted_unix_time BIGINT
);

-- Templates table
CREATE TABLE templates (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    amount BIGINT NOT NULL,
    currency VARCHAR(3) NOT NULL,
    account_id BIGINT REFERENCES accounts(id),
    category_id BIGINT REFERENCES categories(id),
    notes TEXT,
    schedule_type VARCHAR(20) NOT NULL,
    schedule_interval INT,
    schedule_day_of_week INT,
    schedule_day_of_month INT,
    schedule_month INT,
    next_run_time BIGINT,
    enabled BOOLEAN DEFAULT TRUE,
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT NOT NULL,
    created_by BIGINT,
    modified_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE,
    deleted_unix_time BIGINT
);

-- Template tags
CREATE TABLE template_tags (
    template_id BIGINT NOT NULL REFERENCES templates(id),
    tag_id BIGINT NOT NULL REFERENCES tags(id),
    PRIMARY KEY (template_id, tag_id)
);

-- Exchange rates
CREATE TABLE exchange_rates (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    from_currency VARCHAR(3) NOT NULL,
    to_currency VARCHAR(3) NOT NULL,
    rate DECIMAL(20, 10) NOT NULL,
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT NOT NULL,
    CONSTRAINT uk_exchange_rates UNIQUE (user_id, from_currency, to_currency)
);

-- Indexes
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_tokens_user ON tokens(user_id);
CREATE INDEX idx_tokens_token ON tokens(token);
CREATE INDEX idx_accounts_user ON accounts(user_id);
CREATE INDEX idx_accounts_user_archived ON accounts(user_id, archived) WHERE deleted = FALSE;
CREATE INDEX idx_categories_user ON categories(user_id);
CREATE INDEX idx_categories_parent ON categories(parent_id);
CREATE INDEX idx_tags_user ON tags(user_id);
CREATE INDEX idx_transactions_user_time ON transactions(user_id, transaction_time DESC) WHERE deleted = FALSE;
CREATE INDEX idx_transactions_account ON transactions(account_id);
CREATE INDEX idx_transactions_category ON transactions(category_id);
CREATE INDEX idx_budgets_user ON budgets(user_id);
CREATE INDEX idx_templates_user ON templates(user_id);