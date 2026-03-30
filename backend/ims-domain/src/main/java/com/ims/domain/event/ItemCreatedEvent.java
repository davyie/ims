package com.ims.domain.event;

import java.util.UUID;

public class ItemCreatedEvent extends AbstractDomainEvent {
    private final String sku;
    private final String name;

    public ItemCreatedEvent(UUID itemId, String sku, String name) {
        super("ItemCreated", itemId);
        this.sku = sku;
        this.name = name;
    }

    public String getSku() { return sku; }
    public String getName() { return name; }
}
