CREATE TABLE IF NOT EXISTS markets (
    market_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    location TEXT,
    market_type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    description TEXT,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_markets_user_id ON markets(user_id);
CREATE INDEX idx_markets_status ON markets(status);

CREATE TABLE IF NOT EXISTS market_stock (
    market_stock_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    market_id UUID NOT NULL REFERENCES markets(market_id),
    item_id UUID NOT NULL,
    quantity INT NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    last_updated TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_market_stock UNIQUE (market_id, item_id)
);

CREATE INDEX idx_market_stock_market_id ON market_stock(market_id);
CREATE INDEX idx_market_stock_item_id ON market_stock(item_id);
