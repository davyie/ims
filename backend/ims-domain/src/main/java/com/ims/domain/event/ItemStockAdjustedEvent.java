package com.ims.domain.event;

import java.util.UUID;

public class ItemStockAdjustedEvent extends AbstractDomainEvent {
    private final int delta;
    private final int newQuantity;

    public ItemStockAdjustedEvent(UUID itemId, int delta, int newQuantity) {
        super("ItemStockAdjusted", itemId);
        this.delta = delta;
        this.newQuantity = newQuantity;
    }

    public int getDelta() { return delta; }
    public int getNewQuantity() { return newQuantity; }
}
