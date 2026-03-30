package com.ims.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public interface DomainEvent {
    UUID getEventId();
    String getEventType();
    UUID getAggregateId();
    LocalDateTime getOccurredAt();
}
