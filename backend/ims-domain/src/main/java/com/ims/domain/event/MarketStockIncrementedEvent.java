package com.ims.domain.event;

import java.util.UUID;

public class MarketStockIncrementedEvent extends AbstractDomainEvent {
    private final UUID itemId;
    private final int quantity;

    public MarketStockIncrementedEvent(UUID marketId, UUID itemId, int quantity) {
        super("MarketStockIncremented", marketId);
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public UUID getItemId() { return itemId; }
    public int getQuantity() { return quantity; }
}
