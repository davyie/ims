package com.ims.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Category {
    private UUID id;
    private String name;
    private LocalDateTime createdAt;

    public Category() {}

    public Category(UUID id, String name, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public static Category create(String name) {
        return new Category(UUID.randomUUID(), name, LocalDateTime.now());
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setId(UUID id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
