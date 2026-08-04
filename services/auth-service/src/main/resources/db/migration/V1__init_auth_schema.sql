CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    account_status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL,
    token VARCHAR(512) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_refresh_tokens_accounts FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);
