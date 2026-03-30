package com.ims.infrastructure.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "items")
@Getter @Setter
public class ItemJpaEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(unique = true, nullable = false)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    private String category;

    @Column(name = "default_price", precision = 19, scale = 4)
    private BigDecimal defaultPrice;

    @Column(name = "default_currency", length = 3)
    private String defaultCurrency;

    @Column(name = "storage_zone")
    private String storageZone;

    @Column(name = "storage_shelf")
    private String storageShelf;

    @Column(name = "storage_row")
    private Integer storageRow;

    @Column(name = "storage_column")
    private Integer storageColumn;

    @Column(name = "total_storage_stock", nullable = false)
    private int totalStorageStock = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
