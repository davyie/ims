package com.ims.warehouse.adapter.in.kafka;

import com.ims.common.event.EventEnvelope;
import com.ims.warehouse.domain.port.in.StockUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class WarehouseCommandListener {

    private static final Logger log = LoggerFactory.getLogger(WarehouseCommandListener.class);

    private final StockUseCase stockUseCase;

    public WarehouseCommandListener(StockUseCase stockUseCase) {
        this.stockUseCase = stockUseCase;
    }

    @KafkaListener(topics = "ims.warehouse.commands", groupId = "ims-warehouse-service")
    public void handleCommand(EventEnvelope envelope) {
        if (envelope == null || envelope.getEventType() == null) return;

        log.info("Received warehouse command: {}", envelope.getEventType());

        try {
            switch (envelope.getEventType()) {
                case "RESERVE_STOCK_COMMAND" -> handleReserveStock(envelope);
                case "STOCK_TRANSFER_COMMIT_COMMAND" -> handleStockTransferCommit(envelope);
                case "ROLLBACK_RESERVATION_COMMAND" -> handleRollbackReservation(envelope);
                default -> log.warn("Unknown warehouse command type: {}", envelope.getEventType());
            }
        } catch (Exception e) {
            log.error("Failed to process warehouse command {}: {}", envelope.getEventType(), e.getMessage(), e);
        }
    }

    private void handleReserveStock(EventEnvelope envelope) {
        Map<String, Object> payload = envelope.getPayload();
        UUID warehouseId = UUID.fromString((String) payload.get("warehouseId"));
        UUID itemId = UUID.fromString((String) payload.get("itemId"));
        int quantity = ((Number) payload.get("quantity")).intValue();

        stockUseCase.reserveStock(warehouseId, itemId, quantity);
        log.info("Reserved {} units of item {} in warehouse {}", quantity, itemId, warehouseId);
    }

    private void handleStockTransferCommit(EventEnvelope envelope) {
        Map<String, Object> payload = envelope.getPayload();
        UUID warehouseId = UUID.fromString((String) payload.get("warehouseId"));
        UUID itemId = UUID.fromString((String) payload.get("itemId"));
        int quantity = ((Number) payload.get("quantity")).intValue();

        stockUseCase.commitReservation(warehouseId, itemId, quantity);
        log.info("Committed transfer of {} units of item {} from warehouse {}", quantity, itemId, warehouseId);
    }

    private void handleRollbackReservation(EventEnvelope envelope) {
        Map<String, Object> payload = envelope.getPayload();
        UUID warehouseId = UUID.fromString((String) payload.get("warehouseId"));
        UUID itemId = UUID.fromString((String) payload.get("itemId"));
        int quantity = ((Number) payload.get("quantity")).intValue();

        stockUseCase.releaseReservation(warehouseId, itemId, quantity);
        log.info("Rolled back reservation of {} units of item {} in warehouse {}", quantity, itemId, warehouseId);
    }
}
