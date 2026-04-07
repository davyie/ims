package com.ims.market.domain.port.out;

import com.ims.common.event.EventEnvelope;

public interface MarketEventPublisher {

    void publish(EventEnvelope envelope);
}
