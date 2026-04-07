package com.ims.market.adapter.out.kafka;

import com.ims.common.event.EventEnvelope;
import com.ims.market.domain.port.out.MarketEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MarketEventPublisherAdapter implements MarketEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(MarketEventPublisherAdapter.class);
    private static final String EVENTS_TOPIC = "ims.market.events";

    private final KafkaTemplate<String, EventEnvelope> kafkaTemplate;

    public MarketEventPublisherAdapter(KafkaTemplate<String, EventEnvelope> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(EventEnvelope envelope) {
        String key = envelope.getEventId().toString();
        kafkaTemplate.send(EVENTS_TOPIC, key, envelope)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event {} to {}: {}", envelope.getEventType(), EVENTS_TOPIC, ex.getMessage());
                    } else {
                        log.debug("Published event {} to {}", envelope.getEventType(), EVENTS_TOPIC);
                    }
                });
    }
}
