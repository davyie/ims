package com.ims.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private final UUID id;
    private final UUID marketId;
    private final UUID itemId;
    private final TransactionType type;
    private final int quantityDelta;
    private final int stockBefore;
    private final int stockAfter;
    private final String note;
    private final LocalDateTime occurredAt;
    private final String createdBy;

    public Transaction(UUID id, UUID marketId, UUID itemId, TransactionType type,
                       int quantityDelta, int stockBefore, int stockAfter,
                       String note, LocalDateTime occurredAt, String createdBy) {
        this.id = id;
        this.marketId = marketId;
        this.itemId = itemId;
        this.type = type;
        this.quantityDelta = quantityDelta;
        this.stockBefore = stockBefore;
        this.stockAfter = stockAfter;
        this.note = note;
        this.occurredAt = occurredAt;
        this.createdBy = createdBy;
    }

    public static Transaction create(UUID marketId, UUID itemId, TransactionType type,
                                      int quantityDelta, int stockBefore, int stockAfter,
                                      String note, String createdBy) {
        return new Transaction(UUID.randomUUID(), marketId, itemId, type,
            quantityDelta, stockBefore, stockAfter, note, LocalDateTime.now(), createdBy);
    }

    public UUID getId() { return id; }
    public UUID getMarketId() { return marketId; }
    public UUID getItemId() { return itemId; }
    public TransactionType getType() { return type; }
    public int getQuantityDelta() { return quantityDelta; }
    public int getStockBefore() { return stockBefore; }
    public int getStockAfter() { return stockAfter; }
    public String getNote() { return note; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public String getCreatedBy() { return createdBy; }
}
