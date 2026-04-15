ALTER TABLE transactions
    ADD COLUMN sale_price NUMERIC(19, 4),
    ADD COLUMN sale_currency VARCHAR(10);
