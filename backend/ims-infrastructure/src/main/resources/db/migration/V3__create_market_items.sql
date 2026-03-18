CREATE TABLE market_items (
    id UUID PRIMARY KEY,
    market_id UUID NOT NULL REFERENCES markets(id),
    item_id UUID NOT NULL REFERENCES items(id),
    allocated_stock INTEGER NOT NULL DEFAULT 0,
    current_stock INTEGER NOT NULL DEFAULT 0,
    market_price NUMERIC(19, 4),
    market_currency CHAR(3),
    UNIQUE (market_id, item_id)
);
