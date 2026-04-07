package com.ims.user.adapter.out.kafka;

import com.ims.common.event.EventEnvelope;
import com.ims.user.domain.port.out.UserEventPublisher;
import com.ims.user.infrastructure.config.UserProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserEventPublisherAdapter implements UserEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(UserEventPublisherAdapter.class);

    private final KafkaTemplate<String, EventEnvelope> kafkaTemplate;
    private final UserProperties properties;

    public UserEventPublisherAdapter(KafkaTemplate<String, EventEnvelope> kafkaTemplate,
                                     UserProperties properties) {
        this.kafkaTemplate = kafkaTemplate;
        this.properties = properties;
    }

    @Override
    public void publish(EventEnvelope envelope) {
        String topic = properties.getKafkaTopicEvents();
        String key = envelope.getUserId() != null ? envelope.getUserId().toString() : envelope.getEventId().toString();
        kafkaTemplate.send(topic, key, envelope)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event {} to topic {}: {}", envelope.getEventType(), topic, ex.getMessage());
                    } else {
                        log.debug("Published event {} to topic {} partition {}", envelope.getEventType(), topic,
                                result.getRecordMetadata().partition());
                    }
                });
    }
}
