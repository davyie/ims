package com.ims.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class AbstractDomainEvent implements DomainEvent {
    private final UUID eventId;
    private final String eventType;
    private final UUID aggregateId;
    private final LocalDateTime occurredAt;

    protected AbstractDomainEvent(String eventType, UUID aggregateId) {
        this.eventId = UUID.randomUUID();
        this.eventType = eventType;
        this.aggregateId = aggregateId;
        this.occurredAt = LocalDateTime.now();
    }

    @Override public UUID getEventId() { return eventId; }
    @Override public String getEventType() { return eventType; }
    @Override public UUID getAggregateId() { return aggregateId; }
    @Override public LocalDateTime getOccurredAt() { return occurredAt; }
}
