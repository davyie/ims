-- Items: add user_id, change sku unique to per-user
ALTER TABLE items ADD COLUMN user_id UUID REFERENCES users(id);
UPDATE items SET user_id = (SELECT id FROM users ORDER BY created_at LIMIT 1) WHERE user_id IS NULL;
ALTER TABLE items ALTER COLUMN user_id SET NOT NULL;
ALTER TABLE items DROP CONSTRAINT IF EXISTS items_sku_key;
ALTER TABLE items ADD CONSTRAINT items_sku_user_unique UNIQUE (user_id, sku);
CREATE INDEX idx_items_user_id ON items(user_id);

-- Markets: add user_id
ALTER TABLE markets ADD COLUMN user_id UUID REFERENCES users(id);
UPDATE markets SET user_id = (SELECT id FROM users ORDER BY created_at LIMIT 1) WHERE user_id IS NULL;
ALTER TABLE markets ALTER COLUMN user_id SET NOT NULL;
CREATE INDEX idx_markets_user_id ON markets(user_id);

-- Categories: add user_id, change name unique to per-user
ALTER TABLE categories ADD COLUMN user_id UUID REFERENCES users(id);
UPDATE categories SET user_id = (SELECT id FROM users ORDER BY created_at LIMIT 1) WHERE user_id IS NULL;
ALTER TABLE categories ALTER COLUMN user_id SET NOT NULL;
ALTER TABLE categories DROP CONSTRAINT IF EXISTS categories_name_key;
ALTER TABLE categories ADD CONSTRAINT categories_name_user_unique UNIQUE (user_id, name);
CREATE INDEX idx_categories_user_id ON categories(user_id);

-- Transactions: add user_id
ALTER TABLE transactions ADD COLUMN user_id UUID REFERENCES users(id);
UPDATE transactions SET user_id = (SELECT id FROM users ORDER BY created_at LIMIT 1) WHERE user_id IS NULL;
ALTER TABLE transactions ALTER COLUMN user_id SET NOT NULL;
CREATE INDEX idx_transactions_user_id ON transactions(user_id);
