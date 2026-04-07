package com.ims.warehouse.application.service;

import com.ims.common.dto.PageResponse;
import com.ims.common.event.EventEnvelope;
import com.ims.common.exception.ResourceNotFoundException;
import com.ims.warehouse.domain.model.WarehouseStock;
import com.ims.warehouse.domain.port.in.StockUseCase;
import com.ims.warehouse.domain.port.out.StockRepository;
import com.ims.warehouse.domain.port.out.WarehouseEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class StockService implements StockUseCase {

    private final StockRepository stockRepository;
    private final WarehouseEventPublisher eventPublisher;

    public StockService(StockRepository stockRepository, WarehouseEventPublisher eventPublisher) {
        this.stockRepository = stockRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public WarehouseStock addStock(UUID warehouseId, UUID itemId, int quantity, String binLocation) {
        WarehouseStock stock = stockRepository.findByWarehouseIdAndItemId(warehouseId, itemId)
                .orElseGet(() -> WarehouseStock.builder()
                        .warehouseId(warehouseId)
                        .itemId(itemId)
                        .quantity(0)
                        .binLocation(binLocation)
                        .build());

        stock.addQuantity(quantity);
        if (binLocation != null) stock.setBinLocation(binLocation);

        WarehouseStock saved = stockRepository.save(stock);
        publishStockEvent("STOCK_ADDED", warehouseId, itemId, quantity, null);
        return saved;
    }

    @Override
    public WarehouseStock removeStock(UUID warehouseId, UUID itemId, int quantity) {
        WarehouseStock stock = getStockOrThrow(warehouseId, itemId);
        stock.removeQuantity(quantity);
        WarehouseStock saved = stockRepository.save(stock);
        publishStockEvent("STOCK_REMOVED", warehouseId, itemId, quantity, null);
        return saved;
    }

    @Override
    public WarehouseStock reserveStock(UUID warehouseId, UUID itemId, int quantity) {
        WarehouseStock stock = getStockOrThrow(warehouseId, itemId);
        stock.reserve(quantity);
        WarehouseStock saved = stockRepository.save(stock);
        publishStockEvent("STOCK_RESERVED", warehouseId, itemId, quantity, null);
        return saved;
    }

    @Override
    public WarehouseStock releaseReservation(UUID warehouseId, UUID itemId, int quantity) {
        WarehouseStock stock = getStockOrThrow(warehouseId, itemId);
        stock.releaseReservation(quantity);
        WarehouseStock saved = stockRepository.save(stock);
        publishStockEvent("STOCK_RESERVATION_RELEASED", warehouseId, itemId, quantity, null);
        return saved;
    }

    @Override
    public WarehouseStock commitReservation(UUID warehouseId, UUID itemId, int quantity) {
        WarehouseStock stock = getStockOrThrow(warehouseId, itemId);
        stock.commitReservation(quantity);
        WarehouseStock saved = stockRepository.save(stock);
        publishStockEvent("STOCK_DEDUCTED", warehouseId, itemId, quantity, null);
        return saved;
    }

    @Override
    public WarehouseStock adjustStock(UUID warehouseId, UUID itemId, int newQuantity) {
        WarehouseStock stock = getStockOrThrow(warehouseId, itemId);
        int previousQty = stock.getQuantity();
        stock.setQuantity(newQuantity);
        WarehouseStock saved = stockRepository.save(stock);
        publishStockEvent("STOCK_ADJUSTED", warehouseId, itemId, newQuantity, previousQty);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseStock getStock(UUID warehouseId, UUID itemId) {
        return getStockOrThrow(warehouseId, itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<WarehouseStock> listStockByWarehouse(UUID warehouseId, int page, int size) {
        Page<WarehouseStock> result = stockRepository.findByWarehouseId(warehouseId, PageRequest.of(page, size));
        return PageResponse.of(result.getContent(), page, size, result.getTotalElements());
    }

    private WarehouseStock getStockOrThrow(UUID warehouseId, UUID itemId) {
        return stockRepository.findByWarehouseIdAndItemId(warehouseId, itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock for warehouse=" + warehouseId + " item=" + itemId));
    }

    private void publishStockEvent(String eventType, UUID warehouseId, UUID itemId, int quantity, Integer previousQty) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("warehouseId", warehouseId.toString());
        payload.put("itemId", itemId.toString());
        payload.put("quantity", quantity);
        if (previousQty != null) payload.put("previousQuantity", previousQty);

        eventPublisher.publish(EventEnvelope.of(eventType, "ims-warehouse-service", null, payload));
    }
}
