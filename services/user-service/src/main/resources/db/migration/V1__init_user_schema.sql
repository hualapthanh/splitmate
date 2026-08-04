CREATE TABLE profiles (
    user_id UUID PRIMARY KEY,
    full_name VARCHAR(100),
    avatar_url VARCHAR(512),
    phone_number VARCHAR(20),
    bio VARCHAR(500),
    timezone VARCHAR(50) DEFAULT 'UTC',
    locale VARCHAR(10) DEFAULT 'en',
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ
);
