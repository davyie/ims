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
@Table(name = "markets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Market {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "market_id", updatable = false, nullable = false)
    private UUID marketId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "location")
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "market_type", nullable = false, length = 30)
    private MarketType marketType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private MarketStatus status = MarketStatus.SCHEDULED;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    @Builder.Default
    private Long version = 0L;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public void open() {
        if (this.status == MarketStatus.ARCHIVED || this.status == MarketStatus.SUSPENDED) {
            throw new ValidationException("Cannot open market in status: " + this.status);
        }
        this.status = MarketStatus.OPEN;
    }

    public void close() {
        this.status = MarketStatus.CLOSED;
    }

    public boolean isOpen() {
        return this.status == MarketStatus.OPEN;
    }
}
