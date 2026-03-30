package com.ims.infrastructure.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ims.domain.event.DomainEvent;
import com.ims.domain.port.DomainEventPublisherPort;
import com.ims.infrastructure.jpa.entity.OutboxEventJpaEntity;
import com.ims.infrastructure.jpa.repository.OutboxEventJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OutboxEventPublisherAdapter implements DomainEventPublisherPort {

    private final OutboxEventJpaRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public OutboxEventPublisherAdapter(OutboxEventJpaRepository outboxRepository, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(DomainEvent event) {
        try {
            OutboxEventJpaEntity outbox = new OutboxEventJpaEntity();
            outbox.setId(event.getEventId());
            outbox.setEventType(event.getEventType());
            outbox.setAggregateId(event.getAggregateId());
            outbox.setPayload(objectMapper.writeValueAsString(event));
            outbox.setStatus("PENDING");
            outbox.setOccurredAt(LocalDateTime.now());
            outboxRepository.save(outbox);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save outbox event", e);
        }
    }
}
