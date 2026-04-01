package com.ims.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Category {
    private UUID id;
    private UUID userId;
    private String name;
    private LocalDateTime createdAt;

    public Category() {}

    public Category(UUID id, UUID userId, String name, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.createdAt = createdAt;
    }

    public static Category create(UUID userId, String name) {
        return new Category(UUID.randomUUID(), userId, name, LocalDateTime.now());
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getName() { return name; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setId(UUID id) { this.id = id; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
