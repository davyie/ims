package com.ims.warehouse.domain.port.out;

import com.ims.common.event.EventEnvelope;

public interface WarehouseEventPublisher {

    void publish(EventEnvelope envelope);

    void publishCommand(String topic, EventEnvelope envelope);
}
