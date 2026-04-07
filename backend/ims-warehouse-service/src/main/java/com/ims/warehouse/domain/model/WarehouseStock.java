package com.ims.warehouse.domain.model;

import com.ims.common.exception.ValidationException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "warehouse_stock")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseStock {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "stock_id", updatable = false, nullable = false)
    private UUID stockId;

    @Column(name = "warehouse_id", nullable = false)
    private UUID warehouseId;

    @Column(name = "item_id", nullable = false)
    private UUID itemId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "reserved_qty", nullable = false)
    @Builder.Default
    private int reservedQty = 0;

    @Column(name = "bin_location")
    private String binLocation;

    @Column(name = "reorder_level")
    @Builder.Default
    private int reorderLevel = 0;

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

    public void addQuantity(int amount) {
        if (amount < 0) throw new ValidationException("Amount must be non-negative");
        this.quantity += amount;
    }

    public void removeQuantity(int amount) {
        if (amount < 0) throw new ValidationException("Amount must be non-negative");
        if (this.quantity - amount < 0) {
            throw new ValidationException("Insufficient stock: available=" + this.quantity + ", requested=" + amount);
        }
        this.quantity -= amount;
    }

    public void reserve(int amount) {
        int available = this.quantity - this.reservedQty;
        if (available < amount) {
            throw new ValidationException("Insufficient available stock to reserve: available=" + available + ", requested=" + amount);
        }
        this.reservedQty += amount;
    }

    public void releaseReservation(int amount) {
        this.reservedQty = Math.max(0, this.reservedQty - amount);
    }

    public void commitReservation(int amount) {
        if (this.quantity < amount) {
            throw new ValidationException("Insufficient stock to commit");
        }
        this.quantity -= amount;
        this.reservedQty = Math.max(0, this.reservedQty - amount);
    }

    public int getAvailableQuantity() {
        return this.quantity - this.reservedQty;
    }
}
