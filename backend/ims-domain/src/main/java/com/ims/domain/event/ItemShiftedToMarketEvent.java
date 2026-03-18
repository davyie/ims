package com.ims.domain.event;

import java.util.UUID;

public class ItemShiftedToMarketEvent extends AbstractDomainEvent {
    private final UUID marketId;
    private final UUID itemId;
    private final int quantity;

    public ItemShiftedToMarketEvent(UUID marketId, UUID itemId, int quantity) {
        super("ItemShiftedToMarket", marketId);
        this.marketId = marketId;
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public UUID getMarketId() { return marketId; }
    public UUID getItemId() { return itemId; }
    public int getQuantity() { return quantity; }
}
