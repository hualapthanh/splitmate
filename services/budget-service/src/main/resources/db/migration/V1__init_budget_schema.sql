CREATE TABLE budgets (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    group_id UUID,
    scope VARCHAR(20) NOT NULL,
    category VARCHAR(50) NOT NULL DEFAULT 'ALL',
    amount_limit NUMERIC(19, 2) NOT NULL,
    current_spent NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
    period_month VARCHAR(7) NOT NULL,
    alert_80_sent BOOLEAN NOT NULL DEFAULT FALSE,
    alert_90_sent BOOLEAN NOT NULL DEFAULT FALSE,
    alert_100_sent BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_budgets_user_id ON budgets(user_id);
CREATE INDEX idx_budgets_group_id ON budgets(group_id);
CREATE INDEX idx_budgets_period ON budgets(period_month);
