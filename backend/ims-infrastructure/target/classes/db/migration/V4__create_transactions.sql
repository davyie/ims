CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    market_id UUID REFERENCES markets(id),
    item_id UUID NOT NULL REFERENCES items(id),
    type VARCHAR(50) NOT NULL,
    quantity_delta INTEGER NOT NULL,
    stock_before INTEGER NOT NULL,
    stock_after INTEGER NOT NULL,
    note TEXT,
    occurred_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255)
);

CREATE INDEX idx_tx_market_id ON transactions(market_id);
CREATE INDEX idx_tx_item_id ON transactions(item_id);
CREATE INDEX idx_tx_occurred_at ON transactions(occurred_at);
