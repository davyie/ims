package com.ims.domain.event;

import java.util.UUID;

public class MarketStockDecrementedEvent extends AbstractDomainEvent {
    private final UUID itemId;
    private final int quantity;

    public MarketStockDecrementedEvent(UUID marketId, UUID itemId, int quantity) {
        super("MarketStockDecremented", marketId);
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public UUID getItemId() { return itemId; }
    public int getQuantity() { return quantity; }
}
