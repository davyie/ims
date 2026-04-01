package com.ims.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private final UUID id;
    private final UUID userId;
    private final UUID marketId;
    private final UUID itemId;
    private final TransactionType type;
    private final int quantityDelta;
    private final int stockBefore;
    private final int stockAfter;
    private final String note;
    private final LocalDateTime occurredAt;
    private final String createdBy;
    private final BigDecimal salePrice;
    private final String saleCurrency;

    public Transaction(UUID id, UUID userId, UUID marketId, UUID itemId, TransactionType type,
                       int quantityDelta, int stockBefore, int stockAfter,
                       String note, LocalDateTime occurredAt, String createdBy,
                       BigDecimal salePrice, String saleCurrency) {
        this.id = id;
        this.userId = userId;
        this.marketId = marketId;
        this.itemId = itemId;
        this.type = type;
        this.quantityDelta = quantityDelta;
        this.stockBefore = stockBefore;
        this.stockAfter = stockAfter;
        this.note = note;
        this.occurredAt = occurredAt;
        this.createdBy = createdBy;
        this.salePrice = salePrice;
        this.saleCurrency = saleCurrency;
    }

    public static Transaction create(UUID userId, UUID marketId, UUID itemId, TransactionType type,
                                      int quantityDelta, int stockBefore, int stockAfter,
                                      String note, String createdBy) {
        return new Transaction(UUID.randomUUID(), userId, marketId, itemId, type,
            quantityDelta, stockBefore, stockAfter, note, LocalDateTime.now(), createdBy, null, null);
    }

    public static Transaction createSale(UUID userId, UUID marketId, UUID itemId,
                                          int quantityDelta, int stockBefore, int stockAfter,
                                          String note, String createdBy,
                                          BigDecimal salePrice, String saleCurrency) {
        return new Transaction(UUID.randomUUID(), userId, marketId, itemId, TransactionType.SALE,
            quantityDelta, stockBefore, stockAfter, note, LocalDateTime.now(), createdBy,
            salePrice, saleCurrency);
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getMarketId() { return marketId; }
    public UUID getItemId() { return itemId; }
    public TransactionType getType() { return type; }
    public int getQuantityDelta() { return quantityDelta; }
    public int getStockBefore() { return stockBefore; }
    public int getStockAfter() { return stockAfter; }
    public String getNote() { return note; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public String getCreatedBy() { return createdBy; }
    public BigDecimal getSalePrice() { return salePrice; }
    public String getSaleCurrency() { return saleCurrency; }
}
