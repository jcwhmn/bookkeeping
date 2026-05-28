-- V12: Transaction Pictures
-- Store receipt images attached to transactions

CREATE TABLE transaction_pictures (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    transaction_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    mime_type VARCHAR(100),
    created_at BIGINT NOT NULL DEFAULT EXTRACT(EPOCH FROM NOW())::BIGINT,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at BIGINT
);

CREATE INDEX idx_transaction_pictures_user ON transaction_pictures(user_id);
CREATE INDEX idx_transaction_pictures_transaction ON transaction_pictures(transaction_id);