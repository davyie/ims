ALTER TABLE items
    ALTER COLUMN default_currency TYPE VARCHAR(3),
    ALTER COLUMN storage_zone TYPE VARCHAR(100),
    ALTER COLUMN storage_shelf TYPE VARCHAR(100);

ALTER TABLE market_items
    ALTER COLUMN market_currency TYPE VARCHAR(3);
