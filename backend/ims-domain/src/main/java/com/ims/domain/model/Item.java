package com.ims.domain.model;

import com.ims.domain.valueobject.Money;
import com.ims.domain.valueobject.StoragePosition;
import com.ims.domain.valueobject.StockLevel;

import java.time.LocalDateTime;
import java.util.UUID;

public class Item {
    private UUID id;
    private UUID userId;
    private String sku;
    private String name;
    private String description;
    private String category;
    private Money defaultPrice;
    private StoragePosition storagePosition;
    private StockLevel totalStorageStock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Item() {}

    public Item(UUID id, UUID userId, String sku, String name, String description, String category,
                Money defaultPrice, StoragePosition storagePosition, StockLevel totalStorageStock,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.category = category;
        this.defaultPrice = defaultPrice;
        this.storagePosition = storagePosition;
        this.totalStorageStock = totalStorageStock;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Item create(UUID userId, String sku, String name, String description, String category,
                               Money defaultPrice, StoragePosition storagePosition) {
        return new Item(
            UUID.randomUUID(), userId, sku, name, description, category,
            defaultPrice, storagePosition, StockLevel.zero(),
            LocalDateTime.now(), LocalDateTime.now()
        );
    }

    public void update(String name, String description, String category, Money defaultPrice, StoragePosition storagePosition) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.defaultPrice = defaultPrice;
        this.storagePosition = storagePosition;
        this.updatedAt = LocalDateTime.now();
    }

    public void adjustStock(int delta) {
        if (delta >= 0) {
            this.totalStorageStock = this.totalStorageStock.add(delta);
        } else {
            this.totalStorageStock = this.totalStorageStock.subtract(-delta);
        }
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public Money getDefaultPrice() { return defaultPrice; }
    public StoragePosition getStoragePosition() { return storagePosition; }
    public StockLevel getTotalStorageStock() { return totalStorageStock; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setId(UUID id) { this.id = id; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public void setSku(String sku) { this.sku = sku; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category) { this.category = category; }
    public void setDefaultPrice(Money defaultPrice) { this.defaultPrice = defaultPrice; }
    public void setStoragePosition(StoragePosition storagePosition) { this.storagePosition = storagePosition; }
    public void setTotalStorageStock(StockLevel totalStorageStock) { this.totalStorageStock = totalStorageStock; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
