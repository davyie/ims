package com.ims.market.domain.model;

import com.ims.common.exception.ValidationException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "market_stock", uniqueConstraints = {
        @UniqueConstraint(name = "uk_market_stock", columnNames = {"market_id", "item_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketStock {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "market_stock_id", updatable = false, nullable = false)
    private UUID marketStockId;

    @Column(name = "market_id", nullable = false)
    private UUID marketId;

    @Column(name = "item_id", nullable = false)
    private UUID itemId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

    @Version
    @Column(name = "version", nullable = false)
    @Builder.Default
    private Long version = 0L;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = Instant.now();
    }

    public void increment(int amount) {
        if (amount <= 0) throw new ValidationException("Increment amount must be positive");
        this.quantity += amount;
    }

    public void decrement(int amount) {
        if (amount <= 0) throw new ValidationException("Decrement amount must be positive");
        if (this.quantity - amount < 0) {
            throw new ValidationException("Insufficient market stock: available=" + this.quantity + ", requested=" + amount);
        }
        this.quantity -= amount;
    }
}
