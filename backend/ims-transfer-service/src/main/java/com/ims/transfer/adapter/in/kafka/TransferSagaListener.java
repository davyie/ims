package com.ims.transfer.adapter.in.kafka;

import com.ims.common.event.EventEnvelope;
import com.ims.transfer.application.service.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class TransferSagaListener {

    private static final Logger log = LoggerFactory.getLogger(TransferSagaListener.class);

    private final TransferService transferService;

    public TransferSagaListener(TransferService transferService) {
        this.transferService = transferService;
    }

    @KafkaListener(topics = {"ims.warehouse.events", "ims.market.events"},
            groupId = "ims-transfer-service")
    public void handleEvent(EventEnvelope envelope) {
        if (envelope == null || envelope.getEventType() == null) return;
        if (envelope.getCorrelationId() == null) return;

        log.info("Transfer saga received event: {}", envelope.getEventType());

        try {
            switch (envelope.getEventType()) {
                case "STOCK_RESERVED" -> handleStockReserved(envelope);
                case "STOCK_RESERVATION_FAILED" -> handleReservationFailed(envelope);
                case "MARKET_STOCK_RECEIVED_CONFIRMED" -> handleStockReceivedConfirmed(envelope);
                case "STOCK_DEDUCTED" -> handleStockDeducted(envelope);
                default -> {
                    // Not relevant for saga - ignore
                }
            }
        } catch (Exception e) {
            log.error("Error processing saga event {}: {}", envelope.getEventType(), e.getMessage(), e);
        }
    }

    private void handleStockReserved(EventEnvelope envelope) {
        UUID correlationId = envelope.getCorrelationId();
        transferService.handleStockReserved(correlationId);
        log.info("Stock reserved for correlationId: {}", correlationId);
    }

    private void handleReservationFailed(EventEnvelope envelope) {
        UUID correlationId = envelope.getCorrelationId();
        Map<String, Object> payload = envelope.getPayload();
        String reason = payload != null ? (String) payload.getOrDefault("reason", "Stock reservation failed") : "Unknown";
        transferService.handleReservationFailed(correlationId, reason);
        log.info("Reservation failed for correlationId: {}", correlationId);
    }

    private void handleStockReceivedConfirmed(EventEnvelope envelope) {
        UUID correlationId = envelope.getCorrelationId();
        transferService.handleStockReceivedConfirmed(correlationId);
        log.info("Stock received confirmed for correlationId: {}", correlationId);
    }

    private void handleStockDeducted(EventEnvelope envelope) {
        UUID correlationId = envelope.getCorrelationId();
        transferService.handleStockDeducted(correlationId);
        log.info("Stock deducted for correlationId: {}", correlationId);
    }
}
