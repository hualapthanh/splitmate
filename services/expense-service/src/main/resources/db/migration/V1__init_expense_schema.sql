CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    icon VARCHAR(255),
    color VARCHAR(7),
    description TEXT,
    created_by UUID,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Seed System categories (created_by IS NULL)
INSERT INTO categories (name, icon, color, description, created_by) VALUES
('Food & Dining', '🍽️', '#FF6B6B', 'Restaurants, groceries, coffee, and meals', NULL),
('Transport', '🚗', '#4ECDC4', 'Taxi, fuel, public transit, parking, and flights', NULL),
('Entertainment', '🎬', '#95E1D3', 'Movies, events, games, and streaming subscriptions', NULL),
('Utilities & Bills', '💡', '#F38181', 'Electricity, water, internet, and mobile top-ups', NULL),
('Rent & Housing', '🏠', '#AA96DA', 'Apartment rent, maintenance, and home supplies', NULL),
('Healthcare & Fitness', '⚕️', '#FCBAD3', 'Medicine, gym, doctor consultations, and health', NULL),
('Shopping & Personal', '🛍️', '#A8E6CF', 'Clothes, electronics, and personal shopping', NULL),
('Other', '📌', '#FFD3B6', 'Miscellaneous expenses', NULL);

CREATE TABLE expenses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    group_id UUID,
    expense_type VARCHAR(50) NOT NULL DEFAULT 'GROUP',
    description VARCHAR(500) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL CHECK (amount > 0),
    category_id UUID NOT NULL REFERENCES categories(id),
    date DATE NOT NULL,
    status VARCHAR(50) DEFAULT 'CONFIRMED',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE expense_payers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    expense_id UUID NOT NULL REFERENCES expenses(id) ON DELETE CASCADE,
    user_id UUID NOT NULL,
    amount_paid DECIMAL(15, 2) NOT NULL CHECK (amount_paid > 0),
    payment_method VARCHAR(50) DEFAULT 'CASH',
    created_at TIMESTAMPTZ DEFAULT NOW(),

    UNIQUE(expense_id, user_id)
);

CREATE TABLE expense_splits (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    expense_id UUID NOT NULL REFERENCES expenses(id) ON DELETE CASCADE,
    user_id UUID NOT NULL,
    split_type VARCHAR(50) NOT NULL DEFAULT 'EQUAL',
    amount DECIMAL(15, 2) NOT NULL DEFAULT 0 CHECK (amount >= 0),
    percentage DECIMAL(5, 2) NOT NULL DEFAULT 0 CHECK (percentage >= 0 AND percentage <= 100),
    shares DECIMAL(10, 2) NOT NULL DEFAULT 0 CHECK (shares >= 0),
    created_at TIMESTAMPTZ DEFAULT NOW(),

    UNIQUE(expense_id, user_id)
);

CREATE INDEX idx_categories_created_by ON categories(created_by);
CREATE INDEX idx_expenses_user_id ON expenses(user_id);
CREATE INDEX idx_expenses_group_id ON expenses(group_id);
CREATE INDEX idx_expenses_date ON expenses(date);
CREATE INDEX idx_expenses_status ON expenses(status);
CREATE INDEX idx_expense_payers_expense_id ON expense_payers(expense_id);
CREATE INDEX idx_expense_payers_user_id ON expense_payers(user_id);
CREATE INDEX idx_expense_splits_expense_id ON expense_splits(expense_id);
CREATE INDEX idx_expense_splits_user_id ON expense_splits(user_id);
