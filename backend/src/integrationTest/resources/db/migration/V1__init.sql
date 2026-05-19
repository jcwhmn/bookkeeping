-- V1__init.sql - Schema for integration tests
-- This is a copy of the main migration schema

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(32) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    nickname VARCHAR(64),
    password VARCHAR(64) NOT NULL,
    salt VARCHAR(64) NOT NULL,
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

-- Accounts table
CREATE TABLE IF NOT EXISTS accounts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
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

-- Tokens table
CREATE TABLE IF NOT EXISTS tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    token_hash VARCHAR(128) NOT NULL,
    type VARCHAR(20) NOT NULL,
    expires_at BIGINT NOT NULL,
    created_unix_time BIGINT NOT NULL,
    revoked BOOLEAN DEFAULT FALSE
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_accounts_user ON accounts(user_id);
CREATE INDEX IF NOT EXISTS idx_tokens_user ON tokens(user_id);