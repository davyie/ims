package com.ims.infrastructure.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions",
    indexes = {
        @Index(name = "idx_tx_market_id", columnList = "market_id"),
        @Index(name = "idx_tx_item_id", columnList = "item_id"),
        @Index(name = "idx_tx_occurred_at", columnList = "occurred_at"),
        @Index(name = "idx_tx_user_id", columnList = "user_id")
    }
)
@Getter @Setter
public class TransactionJpaEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "market_id", columnDefinition = "uuid")
    private UUID marketId;

    @Column(name = "item_id", nullable = false, columnDefinition = "uuid")
    private UUID itemId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionTypeJpa type;

    @Column(name = "quantity_delta", nullable = false)
    private int quantityDelta;

    @Column(name = "stock_before", nullable = false)
    private int stockBefore;

    @Column(name = "stock_after", nullable = false)
    private int stockAfter;

    @Column(columnDefinition = "text")
    private String note;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "sale_price", precision = 19, scale = 4)
    private BigDecimal salePrice;

    @Column(name = "sale_currency", length = 10)
    private String saleCurrency;

    public enum TransactionTypeJpa {
        STOCK_ADJUSTMENT, SHIFT_TO_MARKET, RETURN_FROM_MARKET, SALE, INCREMENT
    }
}
