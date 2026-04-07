package com.ims.transfer.application.service;

import com.ims.common.dto.PageResponse;
import com.ims.common.event.EventEnvelope;
import com.ims.common.exception.ResourceNotFoundException;
import com.ims.transfer.domain.model.LocationType;
import com.ims.transfer.domain.model.Transfer;
import com.ims.transfer.domain.model.TransferStatus;
import com.ims.transfer.domain.port.out.TransferEventPublisher;
import com.ims.transfer.domain.port.out.TransferRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class TransferService {

    private static final String WAREHOUSE_COMMANDS_TOPIC = "ims.warehouse.commands";
    private static final String MARKET_COMMANDS_TOPIC = "ims.market.commands";
    private static final String TRANSFER_EVENTS_TOPIC = "ims.transfer.events";

    private final TransferRepository transferRepository;
    private final TransferEventPublisher eventPublisher;

    public TransferService(TransferRepository transferRepository,
                           TransferEventPublisher eventPublisher) {
        this.transferRepository = transferRepository;
        this.eventPublisher = eventPublisher;
    }

    public Transfer initiateTransfer(UUID itemId, int quantity,
                                     LocationType sourceType, UUID sourceId,
                                     LocationType destinationType, UUID destinationId,
                                     UUID userId) {
        Transfer transfer = Transfer.builder()
                .itemId(itemId)
                .quantity(quantity)
                .sourceType(sourceType)
                .sourceId(sourceId)
                .destinationType(destinationType)
                .destinationId(destinationId)
                .userId(userId)
                .status(TransferStatus.PENDING)
                .build();

        Transfer saved = transferRepository.save(transfer);

        // Emit ReserveStockCommand to warehouse
        Map<String, Object> payload = new HashMap<>();
        payload.put("transferId", saved.getTransferId().toString());
        payload.put("correlationId", saved.getCorrelationId().toString());
        payload.put("warehouseId", sourceId.toString());
        payload.put("itemId", itemId.toString());
        payload.put("quantity", quantity);

        EventEnvelope command = EventEnvelope.builder()
                .eventId(UUID.randomUUID())
                .correlationId(saved.getCorrelationId())
                .eventType("RESERVE_STOCK_COMMAND")
                .version(1)
                .originService("ims-transfer-service")
                .userId(userId)
                .payload(payload)
                .build();

        eventPublisher.publish(WAREHOUSE_COMMANDS_TOPIC, command);

        return saved;
    }

    @Transactional(readOnly = true)
    public Transfer getTransferById(UUID transferId) {
        return transferRepository.findById(transferId)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer", transferId));
    }

    @Transactional(readOnly = true)
    public PageResponse<Transfer> listTransfers(int page, int size) {
        Page<Transfer> result = transferRepository.findAll(PageRequest.of(page, size));
        return PageResponse.of(result.getContent(), page, size, result.getTotalElements());
    }

    // Called by saga listener
    public Transfer handleStockReserved(UUID correlationId) {
        Transfer transfer = getByCorrelationId(correlationId);
        transfer.setStatus(TransferStatus.RESERVED);
        Transfer saved = transferRepository.save(transfer);

        // Emit StockReceiveRequestedEvent to market
        Map<String, Object> payload = new HashMap<>();
        payload.put("transferId", saved.getTransferId().toString());
        payload.put("correlationId", correlationId.toString());
        payload.put("marketId", saved.getDestinationId().toString());
        payload.put("itemId", saved.getItemId().toString());
        payload.put("quantity", saved.getQuantity());

        EventEnvelope command = EventEnvelope.builder()
                .eventId(UUID.randomUUID())
                .correlationId(correlationId)
                .eventType("STOCK_RECEIVE_REQUESTED")
                .version(1)
                .originService("ims-transfer-service")
                .userId(saved.getUserId())
                .payload(payload)
                .build();

        eventPublisher.publish(MARKET_COMMANDS_TOPIC, command);
        return saved;
    }

    public Transfer handleReservationFailed(UUID correlationId, String reason) {
        Transfer transfer = getByCorrelationId(correlationId);
        transfer.setStatus(TransferStatus.FAILED);
        transfer.setFailureReason(reason);
        Transfer saved = transferRepository.save(transfer);

        publishTransferEvent("TRANSFER_FAILED", saved);
        return saved;
    }

    public Transfer handleStockReceivedConfirmed(UUID correlationId) {
        Transfer transfer = getByCorrelationId(correlationId);
        transfer.setStatus(TransferStatus.IN_PROGRESS);
        Transfer saved = transferRepository.save(transfer);

        // Emit StockTransferCommitCommand to warehouse
        Map<String, Object> payload = new HashMap<>();
        payload.put("transferId", saved.getTransferId().toString());
        payload.put("correlationId", correlationId.toString());
        payload.put("warehouseId", saved.getSourceId().toString());
        payload.put("itemId", saved.getItemId().toString());
        payload.put("quantity", saved.getQuantity());

        EventEnvelope command = EventEnvelope.builder()
                .eventId(UUID.randomUUID())
                .correlationId(correlationId)
                .eventType("STOCK_TRANSFER_COMMIT_COMMAND")
                .version(1)
                .originService("ims-transfer-service")
                .userId(saved.getUserId())
                .payload(payload)
                .build();

        eventPublisher.publish(WAREHOUSE_COMMANDS_TOPIC, command);
        return saved;
    }

    public Transfer handleStockDeducted(UUID correlationId) {
        Transfer transfer = getByCorrelationId(correlationId);
        transfer.setStatus(TransferStatus.COMPLETED);
        Transfer saved = transferRepository.save(transfer);

        publishTransferEvent("TRANSFER_COMPLETED", saved);
        return saved;
    }

    public Transfer handleRollback(UUID correlationId, String reason) {
        Transfer transfer = getByCorrelationId(correlationId);

        // Emit rollback command to warehouse
        Map<String, Object> payload = new HashMap<>();
        payload.put("warehouseId", transfer.getSourceId().toString());
        payload.put("itemId", transfer.getItemId().toString());
        payload.put("quantity", transfer.getQuantity());
        payload.put("correlationId", correlationId.toString());

        EventEnvelope rollback = EventEnvelope.builder()
                .eventId(UUID.randomUUID())
                .correlationId(correlationId)
                .eventType("ROLLBACK_RESERVATION_COMMAND")
                .version(1)
                .originService("ims-transfer-service")
                .payload(payload)
                .build();

        eventPublisher.publish(WAREHOUSE_COMMANDS_TOPIC, rollback);

        transfer.setStatus(TransferStatus.ROLLED_BACK);
        transfer.setFailureReason(reason);
        return transferRepository.save(transfer);
    }

    private Transfer getByCorrelationId(UUID correlationId) {
        return transferRepository.findByCorrelationId(correlationId)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer with correlationId: " + correlationId, "TRANSFER_NOT_FOUND"));
    }

    private void publishTransferEvent(String eventType, Transfer transfer) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("transferId", transfer.getTransferId().toString());
        payload.put("correlationId", transfer.getCorrelationId().toString());
        payload.put("status", transfer.getStatus().name());

        EventEnvelope event = EventEnvelope.of(eventType, "ims-transfer-service", transfer.getUserId(), payload);
        eventPublisher.publish(TRANSFER_EVENTS_TOPIC, event);
    }
}
