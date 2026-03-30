package com.ims.domain.model;

import com.ims.domain.exception.InsufficientStockException;
import com.ims.domain.valueobject.Money;
import com.ims.domain.valueobject.StockLevel;

import java.util.UUID;

public class MarketItem {
    private UUID id;
    private UUID marketId;
    private UUID itemId;
    private StockLevel allocatedStock;
    private StockLevel currentStock;
    private Money marketPrice;

    public MarketItem() {}

    public MarketItem(UUID id, UUID marketId, UUID itemId, StockLevel allocatedStock,
                      StockLevel currentStock, Money marketPrice) {
        this.id = id;
        this.marketId = marketId;
        this.itemId = itemId;
        this.allocatedStock = allocatedStock;
        this.currentStock = currentStock;
        this.marketPrice = marketPrice;
    }

    public static MarketItem create(UUID marketId, UUID itemId, int quantity, Money marketPrice) {
        StockLevel stock = StockLevel.of(quantity);
        return new MarketItem(UUID.randomUUID(), marketId, itemId, stock, stock, marketPrice);
    }

    public void addStock(int quantity) {
        this.allocatedStock = this.allocatedStock.add(quantity);
        this.currentStock = this.currentStock.add(quantity);
    }

    public void increment(int quantity) {
        this.currentStock = this.currentStock.add(quantity);
    }

    public void decrement(int quantity) {
        if (quantity > this.currentStock.quantity()) {
            throw new InsufficientStockException(
                "Cannot decrement " + quantity + " from market stock of " + this.currentStock.quantity());
        }
        this.currentStock = this.currentStock.subtract(quantity);
    }

    public void setMarketPrice(Money price) {
        this.marketPrice = price;
    }

    public UUID getId() { return id; }
    public UUID getMarketId() { return marketId; }
    public UUID getItemId() { return itemId; }
    public StockLevel getAllocatedStock() { return allocatedStock; }
    public StockLevel getCurrentStock() { return currentStock; }
    public Money getMarketPrice() { return marketPrice; }
    public void setId(UUID id) { this.id = id; }
    public void setMarketId(UUID marketId) { this.marketId = marketId; }
    public void setItemId(UUID itemId) { this.itemId = itemId; }
    public void setAllocatedStock(StockLevel allocatedStock) { this.allocatedStock = allocatedStock; }
    public void setCurrentStock(StockLevel currentStock) { this.currentStock = currentStock; }
}
