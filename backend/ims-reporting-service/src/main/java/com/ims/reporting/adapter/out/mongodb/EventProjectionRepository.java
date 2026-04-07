package com.ims.reporting.adapter.out.mongodb;

import com.ims.reporting.domain.model.EventProjectionDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventProjectionRepository extends MongoRepository<EventProjectionDocument, String> {

    Page<EventProjectionDocument> findByOriginService(String originService, Pageable pageable);

    Page<EventProjectionDocument> findByEntityId(UUID entityId, Pageable pageable);

    List<EventProjectionDocument> findByEntityIdAndEventTypeIn(UUID entityId, List<String> eventTypes);

    List<EventProjectionDocument> findByOriginServiceAndOccurredAtBetween(
            String originService, Instant from, Instant to);

    boolean existsByEventId(UUID eventId);
}
