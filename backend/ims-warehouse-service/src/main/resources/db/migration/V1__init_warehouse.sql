CREATE TABLE IF NOT EXISTS warehouses (
    warehouse_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    address TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_warehouses_user_id ON warehouses(user_id);
CREATE INDEX idx_warehouses_status ON warehouses(status);

CREATE TABLE IF NOT EXISTS items (
    item_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    sku VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    unit_of_measure VARCHAR(50),
    unit_price NUMERIC(19, 4),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_items_user_sku UNIQUE (user_id, sku)
);

CREATE INDEX idx_items_user_id ON items(user_id);
CREATE INDEX idx_items_sku ON items(sku);
CREATE INDEX idx_items_category ON items(category);

CREATE TABLE IF NOT EXISTS warehouse_stock (
    stock_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    warehouse_id UUID NOT NULL REFERENCES warehouses(warehouse_id),
    item_id UUID NOT NULL REFERENCES items(item_id),
    quantity INT NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    reserved_qty INT NOT NULL DEFAULT 0,
    bin_location VARCHAR(100),
    reorder_level INT NOT NULL DEFAULT 0,
    last_updated TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_warehouse_stock UNIQUE (warehouse_id, item_id)
);

CREATE INDEX idx_warehouse_stock_warehouse_id ON warehouse_stock(warehouse_id);
CREATE INDEX idx_warehouse_stock_item_id ON warehouse_stock(item_id);
