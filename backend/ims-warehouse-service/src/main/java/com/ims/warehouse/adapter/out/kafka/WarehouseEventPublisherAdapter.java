package com.ims.warehouse.adapter.out.kafka;

import com.ims.common.event.EventEnvelope;
import com.ims.warehouse.domain.port.out.WarehouseEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class WarehouseEventPublisherAdapter implements WarehouseEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(WarehouseEventPublisherAdapter.class);
    private static final String EVENTS_TOPIC = "ims.warehouse.events";

    private final KafkaTemplate<String, EventEnvelope> kafkaTemplate;

    public WarehouseEventPublisherAdapter(KafkaTemplate<String, EventEnvelope> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(EventEnvelope envelope) {
        publish(EVENTS_TOPIC, envelope);
    }

    @Override
    public void publishCommand(String topic, EventEnvelope envelope) {
        publish(topic, envelope);
    }

    private void publish(String topic, EventEnvelope envelope) {
        String key = envelope.getEventId().toString();
        kafkaTemplate.send(topic, key, envelope)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event {} to topic {}: {}", envelope.getEventType(), topic, ex.getMessage());
                    } else {
                        log.debug("Published event {} to topic {}", envelope.getEventType(), topic);
                    }
                });
    }
}
