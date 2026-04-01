package com.ims.domain.model;

import com.ims.domain.exception.InvalidMarketStateException;

import java.time.LocalDateTime;
import java.util.UUID;

public class Market {
    private UUID id;
    private UUID userId;
    private String name;
    private String place;
    private LocalDateTime openDate;
    private LocalDateTime closeDate;
    private MarketStatus status;
    private LocalDateTime createdAt;

    public Market() {}

    public Market(UUID id, UUID userId, String name, String place, LocalDateTime openDate, LocalDateTime closeDate,
                  MarketStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.place = place;
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Market create(UUID userId, String name, String place, LocalDateTime openDate, LocalDateTime closeDate) {
        return new Market(UUID.randomUUID(), userId, name, place, openDate, closeDate, MarketStatus.SCHEDULED, LocalDateTime.now());
    }

    public void update(String name, String place, LocalDateTime openDate, LocalDateTime closeDate) {
        if (this.status != MarketStatus.SCHEDULED) {
            throw new InvalidMarketStateException("Market can only be edited in SCHEDULED state, current: " + status);
        }
        this.name = name;
        this.place = place;
        this.openDate = openDate;
        this.closeDate = closeDate;
    }

    public void open() {
        if (status != MarketStatus.SCHEDULED) {
            throw new InvalidMarketStateException("Market can only be opened from SCHEDULED state, current: " + status);
        }
        this.status = MarketStatus.OPEN;
    }

    public void close() {
        if (status != MarketStatus.OPEN) {
            throw new InvalidMarketStateException("Market can only be closed from OPEN state, current: " + status);
        }
        this.status = MarketStatus.CLOSED;
    }

    public boolean isOpen() { return status == MarketStatus.OPEN; }
    public boolean isScheduled() { return status == MarketStatus.SCHEDULED; }
    public boolean isClosed() { return status == MarketStatus.CLOSED; }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getName() { return name; }
    public String getPlace() { return place; }
    public LocalDateTime getOpenDate() { return openDate; }
    public LocalDateTime getCloseDate() { return closeDate; }
    public MarketStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setId(UUID id) { this.id = id; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setPlace(String place) { this.place = place; }
    public void setOpenDate(LocalDateTime openDate) { this.openDate = openDate; }
    public void setCloseDate(LocalDateTime closeDate) { this.closeDate = closeDate; }
    public void setStatus(MarketStatus status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
