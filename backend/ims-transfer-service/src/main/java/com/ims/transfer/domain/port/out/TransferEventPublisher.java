package com.ims.transfer.domain.port.out;

import com.ims.common.event.EventEnvelope;

public interface TransferEventPublisher {

    void publish(String topic, EventEnvelope envelope);
}
