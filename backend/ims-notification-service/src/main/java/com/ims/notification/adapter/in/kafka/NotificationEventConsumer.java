package com.ims.notification.adapter.in.kafka;

import com.ims.common.event.EventEnvelope;
import com.ims.notification.adapter.out.redis.NotificationIdempotencyCache;
import com.ims.notification.application.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Consumes critical domain events and dispatches user-facing notifications.
 * Uses Redis idempotency cache to prevent duplicate notifications.
 */
@Component
public class NotificationEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventConsumer.class);

    private final NotificationService notificationService;
    private final NotificationIdempotencyCache idempotencyCache;

    public NotificationEventConsumer(NotificationService notificationService,
                                     NotificationIdempotencyCache idempotencyCache) {
        this.notificationService = notificationService;
        this.idempotencyCache = idempotencyCache;
    }

    @KafkaListener(
            topics = {"ims.warehouse.events", "ims.transfer.events", "ims.market.events"},
            groupId = "ims-notification-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleEvent(EventEnvelope envelope) {
        if (envelope == null || envelope.getEventId() == null) {
            return;
        }

        // Idempotency check
        if (idempotencyCache.checkAndMark(envelope.getEventId())) {
            log.debug("Duplicate event {} ignored by notification service", envelope.getEventId());
            return;
        }

        try {
            switch (envelope.getEventType()) {
                case "LOW_STOCK_ALERT" -> handleLowStockAlert(envelope);
                case "TRANSFER_FAILED", "TRANSFER_ROLLED_BACK" -> handleTransferFailed(envelope);
                case "MARKET_OPENED" -> handleMarketOpened(envelope);
                case "MARKET_CLOSED" -> handleMarketClosed(envelope);
                default -> log.debug("No notification handler for event type: {}", envelope.getEventType());
            }
        } catch (Exception e) {
            log.error("Error processing notification for event {}: {}", envelope.getEventId(), e.getMessage(), e);
        }
    }

    private void handleLowStockAlert(EventEnvelope envelope) {
        Map<String, Object> payload = envelope.getPayload();
        if (payload == null) return;

        String recipientEmail = (String) payload.getOrDefault("userEmail", "admin@ims.local");
        String itemName = (String) payload.getOrDefault("itemName", "Unknown Item");
        int currentQty = ((Number) payload.getOrDefault("quantity", 0)).intValue();
        int reorderLevel = ((Number) payload.getOrDefault("reorderLevel", 0)).intValue();

        notificationService.sendLowStockAlert(recipientEmail, itemName, currentQty, reorderLevel);
    }

    private void handleTransferFailed(EventEnvelope envelope) {
        Map<String, Object> payload = envelope.getPayload();
        if (payload == null) return;

        String recipientEmail = (String) payload.getOrDefault("userEmail", "admin@ims.local");
        String transferId = (String) payload.getOrDefault("transferId", "unknown");
        String reason = (String) payload.getOrDefault("failureReason", "Unknown reason");

        notificationService.sendTransferFailedAlert(recipientEmail, transferId, reason);
    }

    private void handleMarketOpened(EventEnvelope envelope) {
        Map<String, Object> payload = envelope.getPayload();
        if (payload == null) return;

        String recipientEmail = (String) payload.getOrDefault("userEmail", "admin@ims.local");
        String marketName = (String) payload.getOrDefault("marketName", "Market");

        notificationService.sendMarketSessionNotification(recipientEmail, marketName, "OPENED");
    }

    private void handleMarketClosed(EventEnvelope envelope) {
        Map<String, Object> payload = envelope.getPayload();
        if (payload == null) return;

        String recipientEmail = (String) payload.getOrDefault("userEmail", "admin@ims.local");
        String marketName = (String) payload.getOrDefault("marketName", "Market");

        notificationService.sendMarketSessionNotification(recipientEmail, marketName, "CLOSED");
    }
}
