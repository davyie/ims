package com.ims.infrastructure.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "market_items", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"market_id", "item_id"})
})
@Getter @Setter
public class MarketItemJpaEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "market_id", nullable = false, columnDefinition = "uuid")
    private UUID marketId;

    @Column(name = "item_id", nullable = false, columnDefinition = "uuid")
    private UUID itemId;

    @Column(name = "allocated_stock", nullable = false)
    private int allocatedStock;

    @Column(name = "current_stock", nullable = false)
    private int currentStock;

    @Column(name = "market_price", precision = 19, scale = 4)
    private BigDecimal marketPrice;

    @Column(name = "market_currency", length = 3)
    private String marketCurrency;
}
