package com.ims.domain.model;

import com.ims.domain.exception.InvalidMarketStateException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Market {
    private UUID id;
    private String name;
    private String place;
    private LocalDate openDate;
    private LocalDate closeDate;
    private MarketStatus status;
    private LocalDateTime createdAt;

    public Market() {}

    public Market(UUID id, String name, String place, LocalDate openDate, LocalDate closeDate,
                  MarketStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.place = place;
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Market create(String name, String place, LocalDate openDate, LocalDate closeDate) {
        return new Market(UUID.randomUUID(), name, place, openDate, closeDate, MarketStatus.SCHEDULED, LocalDateTime.now());
    }

    public void update(String name, String place, LocalDate openDate, LocalDate closeDate) {
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
    public String getName() { return name; }
    public String getPlace() { return place; }
    public LocalDate getOpenDate() { return openDate; }
    public LocalDate getCloseDate() { return closeDate; }
    public MarketStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setId(UUID id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPlace(String place) { this.place = place; }
    public void setOpenDate(LocalDate openDate) { this.openDate = openDate; }
    public void setCloseDate(LocalDate closeDate) { this.closeDate = closeDate; }
    public void setStatus(MarketStatus status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
