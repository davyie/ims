package com.ims.transfer.adapter.out.kafka;

import com.ims.common.event.EventEnvelope;
import com.ims.transfer.domain.port.out.TransferEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransferEventPublisherAdapter implements TransferEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(TransferEventPublisherAdapter.class);

    private final KafkaTemplate<String, EventEnvelope> kafkaTemplate;

    public TransferEventPublisherAdapter(KafkaTemplate<String, EventEnvelope> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(String topic, EventEnvelope envelope) {
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
