package com.ims.infrastructure.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_events",
    indexes = {
        @Index(name = "idx_outbox_status", columnList = "status")
    }
)
@Getter @Setter
public class OutboxEventJpaEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "aggregate_id", nullable = false, columnDefinition = "uuid")
    private UUID aggregateId;

    @Column(nullable = false, columnDefinition = "text")
    private String payload;

    @Column(nullable = false)
    private String status; // PENDING, PUBLISHED, FAILED

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;
}
