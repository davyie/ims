package com.ims.transaction.adapter.in.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ims.common.event.EventEnvelope;
import com.ims.transaction.adapter.out.persistence.TransactionJpaRepository;
import com.ims.transaction.domain.model.TransactionRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionLedgerConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransactionLedgerConsumer.class);

    private final TransactionJpaRepository repository;
    private final ObjectMapper objectMapper;

    public TransactionLedgerConsumer(TransactionJpaRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    /**
     * Wildcard consumer for ALL ims.* topics — persists every event as an immutable ledger entry.
     */
    @KafkaListener(
            topicPattern = "ims\\..*",
            groupId = "ims-transaction-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, EventEnvelope> record) {
        EventEnvelope envelope = record.value();
        if (envelope == null || envelope.getEventId() == null) {
            log.warn("Received null or invalid envelope from topic {}", record.topic());
            return;
        }

        // Idempotency: skip if already persisted
        if (repository.existsByEventId(envelope.getEventId())) {
            log.debug("Duplicate event {} skipped", envelope.getEventId());
            return;
        }

        try {
            String payloadJson = objectMapper.writeValueAsString(envelope.getPayload());

            TransactionRecord txRecord = TransactionRecord.builder()
                    .eventId(envelope.getEventId())
                    .correlationId(envelope.getCorrelationId())
                    .eventType(envelope.getEventType())
                    .originService(envelope.getOriginService())
                    .userId(envelope.getUserId())
                    .occurredAt(envelope.getOccurredAt())
                    .payload(payloadJson)
                    .kafkaTopic(record.topic())
                    .kafkaPartition(record.partition())
                    .build();

            repository.save(txRecord);
            log.debug("Persisted event {} from topic {}", envelope.getEventId(), record.topic());

        } catch (Exception e) {
            log.error("Failed to persist event {} from topic {}: {}", envelope.getEventId(), record.topic(), e.getMessage(), e);
            throw new RuntimeException("Failed to persist transaction record", e);
        }
    }
}
