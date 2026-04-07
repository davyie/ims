package com.ims.reporting.adapter.in.kafka;

import com.ims.common.event.EventEnvelope;
import com.ims.reporting.adapter.out.mongodb.EventProjectionRepository;
import com.ims.reporting.domain.model.EventProjectionDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
public class ReportingEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ReportingEventConsumer.class);

    private final EventProjectionRepository repository;

    public ReportingEventConsumer(EventProjectionRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(
            topics = {"ims.warehouse.events", "ims.market.events", "ims.transfer.events"},
            groupId = "ims-reporting-service"
    )
    public void consumeEvent(EventEnvelope envelope) {
        if (envelope == null || envelope.getEventId() == null) return;

        // Idempotency check
        if (repository.existsByEventId(envelope.getEventId())) {
            log.debug("Skipping duplicate event: {}", envelope.getEventId());
            return;
        }

        try {
            UUID entityId = extractEntityId(envelope);

            EventProjectionDocument doc = EventProjectionDocument.builder()
                    .eventId(envelope.getEventId())
                    .eventType(envelope.getEventType())
                    .originService(envelope.getOriginService())
                    .entityId(entityId)
                    .userId(envelope.getUserId())
                    .occurredAt(envelope.getOccurredAt())
                    .recordedAt(Instant.now())
                    .payload(envelope.getPayload())
                    .build();

            repository.save(doc);
            log.debug("Persisted event projection for {} id={}", envelope.getEventType(), envelope.getEventId());

        } catch (Exception e) {
            log.error("Failed to persist event projection {}: {}", envelope.getEventType(), e.getMessage(), e);
        }
    }

    private UUID extractEntityId(EventEnvelope envelope) {
        Map<String, Object> payload = envelope.getPayload();
        if (payload == null) return null;

        String[] possibleKeys = {"warehouseId", "marketId", "transferId", "itemId"};
        for (String key : possibleKeys) {
            Object value = payload.get(key);
            if (value instanceof String s) {
                try {
                    return UUID.fromString(s);
                } catch (IllegalArgumentException ignored) {}
            }
        }
        return null;
    }
}
