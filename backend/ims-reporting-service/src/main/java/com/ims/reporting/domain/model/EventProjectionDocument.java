package com.ims.reporting.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Document(collection = "event_projections")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventProjectionDocument {

    @Id
    private String id;

    @Indexed
    private UUID eventId;

    @Indexed
    private String eventType;

    @Indexed
    private String originService;

    @Indexed
    private UUID entityId;

    private UUID userId;

    @Indexed
    private Instant occurredAt;

    private Instant recordedAt;

    private Map<String, Object> payload;
}
