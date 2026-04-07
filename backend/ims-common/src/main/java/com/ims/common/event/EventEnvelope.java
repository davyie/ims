package com.ims.common.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventEnvelope {

    private UUID eventId;
    private UUID correlationId;
    private String eventType;
    private int version;
    private String originService;
    private UUID userId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant occurredAt;

    /**
     * Payload stored as Map<String, Object> for JSON serialization simplicity.
     * Consumers should cast or deserialize based on eventType.
     */
    private Map<String, Object> payload;

    public static EventEnvelope of(String eventType, String originService, UUID userId, Map<String, Object> payload) {
        return EventEnvelope.builder()
                .eventId(UUID.randomUUID())
                .correlationId(UUID.randomUUID())
                .eventType(eventType)
                .version(1)
                .originService(originService)
                .userId(userId)
                .occurredAt(Instant.now())
                .payload(payload)
                .build();
    }
}
