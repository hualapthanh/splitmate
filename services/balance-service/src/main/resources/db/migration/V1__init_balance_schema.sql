CREATE TABLE group_balances (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    group_id UUID NOT NULL,
    user_id UUID NOT NULL,
    net_balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),

    UNIQUE(group_id, user_id)
);

CREATE TABLE user_debts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    group_id UUID NOT NULL,
    debtor_id UUID NOT NULL,
    creditor_id UUID NOT NULL,
    amount DECIMAL(15, 2) NOT NULL DEFAULT 0.00 CHECK (amount >= 0),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),

    UNIQUE(group_id, debtor_id, creditor_id)
);

CREATE TABLE settlements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    group_id UUID,
    payer_id UUID NOT NULL,
    payee_id UUID NOT NULL,
    amount DECIMAL(15, 2) NOT NULL CHECK (amount > 0),
    currency VARCHAR(3) DEFAULT 'VND',
    payment_method VARCHAR(50) DEFAULT 'BANK_TRANSFER',
    status VARCHAR(50) DEFAULT 'COMPLETED',
    settled_at TIMESTAMPTZ DEFAULT NOW(),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_group_balances_group_id ON group_balances(group_id);
CREATE INDEX idx_group_balances_user_id ON group_balances(user_id);
CREATE INDEX idx_user_debts_group_id ON user_debts(group_id);
CREATE INDEX idx_user_debts_debtor ON user_debts(debtor_id);
CREATE INDEX idx_user_debts_creditor ON user_debts(creditor_id);
CREATE INDEX idx_settlements_group_id ON settlements(group_id);
CREATE INDEX idx_settlements_payer ON settlements(payer_id);
CREATE INDEX idx_settlements_payee ON settlements(payee_id);
