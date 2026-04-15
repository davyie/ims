package com.ims.reporting.application.service;

import com.ims.common.dto.PageResponse;
import com.ims.reporting.adapter.out.mongodb.EventProjectionRepository;
import com.ims.reporting.domain.model.EventProjectionDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ReportingService {

    private final EventProjectionRepository repository;

    public ReportingService(EventProjectionRepository repository) {
        this.repository = repository;
    }

    public PageResponse<EventProjectionDocument> getWarehouseInventorySnapshot(UUID warehouseId, int page, int size) {
        List<String> eventTypes = List.of("STOCK_ADDED", "STOCK_REMOVED", "STOCK_ADJUSTED", "STOCK_DEDUCTED");
        List<EventProjectionDocument> results = repository.findByEntityIdAndEventTypeIn(warehouseId, eventTypes);
        return toPageResponse(results, page, size);
    }

    public PageResponse<EventProjectionDocument> getWarehouseMovementHistory(UUID warehouseId, int page, int size) {
        List<String> eventTypes = List.of("STOCK_ADDED", "STOCK_REMOVED", "STOCK_RESERVED",
                "STOCK_DEDUCTED", "STOCK_ADJUSTED");
        List<EventProjectionDocument> results = repository.findByEntityIdAndEventTypeIn(warehouseId, eventTypes);
        return toPageResponse(results, page, size);
    }

    public PageResponse<EventProjectionDocument> getLowStockReport(UUID warehouseId, int page, int size) {
        List<String> eventTypes = List.of("LOW_STOCK_ALERT", "STOCK_ADJUSTED");
        List<EventProjectionDocument> results = repository.findByEntityIdAndEventTypeIn(warehouseId, eventTypes);
        return toPageResponse(results, page, size);
    }

    public PageResponse<EventProjectionDocument> getWarehouseValuation(UUID warehouseId, int page, int size) {
        return getWarehouseInventorySnapshot(warehouseId, page, size);
    }

    public PageResponse<EventProjectionDocument> getMarketSessionReport(UUID marketId, int page, int size) {
        List<String> eventTypes = List.of("MARKET_OPENED", "MARKET_CLOSED");
        List<EventProjectionDocument> results = repository.findByEntityIdAndEventTypeIn(marketId, eventTypes);
        return toPageResponse(results, page, size);
    }

    public PageResponse<EventProjectionDocument> getMarketStockComparison(UUID marketId, int page, int size) {
        List<String> eventTypes = List.of("MARKET_STOCK_INCREMENTED", "MARKET_STOCK_DECREMENTED", "MARKET_STOCK_RECEIVED_CONFIRMED");
        List<EventProjectionDocument> results = repository.findByEntityIdAndEventTypeIn(marketId, eventTypes);
        return toPageResponse(results, page, size);
    }

    public PageResponse<EventProjectionDocument> getAllMarketSalesEvents(int page, int size) {
        List<EventProjectionDocument> results =
                repository.findByEventTypeOrderByOccurredAtDesc("MARKET_STOCK_DECREMENTED");
        return toPageResponse(results, page, size);
    }

    public PageResponse<EventProjectionDocument> getTransferHistoryReport(int page, int size) {
        Page<EventProjectionDocument> result = repository.findByOriginService("ims-transfer-service", PageRequest.of(page, size));
        return PageResponse.of(result.getContent(), page, size, result.getTotalElements());
    }

    public PageResponse<EventProjectionDocument> getEventsByEntityId(UUID entityId, int page, int size) {
        Page<EventProjectionDocument> result = repository.findByEntityId(entityId, PageRequest.of(page, size));
        return PageResponse.of(result.getContent(), page, size, result.getTotalElements());
    }

    private PageResponse<EventProjectionDocument> toPageResponse(List<EventProjectionDocument> all, int page, int size) {
        long total = all.size();
        int start = page * size;
        int end = Math.min(start + size, (int) total);
        List<EventProjectionDocument> slice = start < total ? all.subList(start, end) : List.of();
        return PageResponse.of(slice, page, size, total);
    }
}
