CREATE TABLE items (
    id UUID PRIMARY KEY,
    sku VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(255),
    default_price NUMERIC(19, 4),
    default_currency CHAR(3),
    storage_zone VARCHAR(100),
    storage_shelf VARCHAR(100),
    storage_row INTEGER,
    storage_column INTEGER,
    total_storage_stock INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
