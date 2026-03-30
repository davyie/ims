package com.ims.domain.valueobject;

public record StockLevel(int quantity) {
    public StockLevel {
        if (quantity < 0) {
            throw new IllegalArgumentException("Stock quantity must be >= 0, got: " + quantity);
        }
    }

    public StockLevel add(int delta) {
        return new StockLevel(this.quantity + delta);
    }

    public StockLevel subtract(int delta) {
        return new StockLevel(this.quantity - delta);
    }

    public static StockLevel of(int quantity) {
        return new StockLevel(quantity);
    }

    public static StockLevel zero() {
        return new StockLevel(0);
    }
}
