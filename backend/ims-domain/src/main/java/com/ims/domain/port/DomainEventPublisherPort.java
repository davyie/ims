package com.ims.domain.port;

import com.ims.domain.event.DomainEvent;

public interface DomainEventPublisherPort {
    void publish(DomainEvent event);
}
