-- Categories table
CREATE TABLE IF NOT EXISTS categories (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('INCOME', 'EXPENSE'))
);

-- Transactions table
CREATE TABLE IF NOT EXISTS transactions (
    id UUID PRIMARY KEY,
    category_id UUID NOT NULL REFERENCES categories(id),
    type VARCHAR(20) NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    amount DECIMAL(19,4) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'BRL',
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_transactions_category_id ON transactions(category_id);
CREATE INDEX IF NOT EXISTS idx_transactions_type ON transactions(type);
CREATE INDEX IF NOT EXISTS idx_transactions_created_at ON transactions(created_at);

-- Seed data
INSERT INTO categories (id, name, type) VALUES
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Alimentação', 'EXPENSE'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Transporte', 'EXPENSE'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Moradia', 'EXPENSE'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'Lazer', 'EXPENSE'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 'Salário', 'INCOME'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16', 'Freelance', 'INCOME')
ON CONFLICT (id) DO NOTHING;
