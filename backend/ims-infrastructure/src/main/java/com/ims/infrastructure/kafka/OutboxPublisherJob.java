package com.ims.infrastructure.kafka;

import com.ims.infrastructure.jpa.entity.OutboxEventJpaEntity;
import com.ims.infrastructure.jpa.repository.OutboxEventJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class OutboxPublisherJob {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisherJob.class);

    private static final Map<String, String> TOPIC_ROUTING = Map.of(
        "ItemCreated", "ims.items",
        "ItemStockAdjusted", "ims.items",
        "MarketOpened", "ims.markets",
        "MarketClosed", "ims.markets",
        "ItemShiftedToMarket", "ims.market-stock",
        "MarketStockIncremented", "ims.market-stock",
        "MarketStockDecremented", "ims.market-stock"
    );

    private final OutboxEventJpaRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxPublisherJob(OutboxEventJpaRepository outboxRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelayString = "${ims.outbox.poll-ms:500}")
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEventJpaEntity> pending = outboxRepository.findByStatus("PENDING");
        for (OutboxEventJpaEntity event : pending) {
            try {
                String topic = TOPIC_ROUTING.getOrDefault(event.getEventType(), "ims.events");
                kafkaTemplate.send(topic, event.getAggregateId().toString(), event.getPayload());
                event.setStatus("PUBLISHED");
                event.setPublishedAt(LocalDateTime.now());
                outboxRepository.save(event);
                log.debug("Published outbox event {} of type {} to topic {}", event.getId(), event.getEventType(), topic);
            } catch (Exception e) {
                log.error("Failed to publish outbox event {}: {}", event.getId(), e.getMessage());
                event.setStatus("FAILED");
                outboxRepository.save(event);
            }
        }
    }
}
