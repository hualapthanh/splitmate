CREATE TABLE analytics_expense_records (
    id UUID PRIMARY KEY,
    expense_id UUID NOT NULL,
    user_id UUID NOT NULL,
    group_id UUID,
    category VARCHAR(50) NOT NULL DEFAULT 'OTHER',
    amount NUMERIC(19, 2) NOT NULL,
    expense_date DATE NOT NULL,
    period_month VARCHAR(7) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE analytics_monthly_summaries (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    period_month VARCHAR(7) NOT NULL,
    total_spent NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
    total_transactions INT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_period UNIQUE (user_id, period_month)
);

CREATE INDEX idx_analytics_records_user_period ON analytics_expense_records(user_id, period_month);
CREATE INDEX idx_analytics_records_group ON analytics_expense_records(group_id);
CREATE INDEX idx_analytics_records_category ON analytics_expense_records(category);
