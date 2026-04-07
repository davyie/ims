package com.ims.user.domain.port.out;

import com.ims.common.event.EventEnvelope;

public interface UserEventPublisher {

    void publish(EventEnvelope envelope);
}
