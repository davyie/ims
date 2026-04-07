package com.ims.warehouse.application.service;

import com.ims.common.dto.PageResponse;
import com.ims.common.event.EventEnvelope;
import com.ims.common.exception.ResourceNotFoundException;
import com.ims.common.exception.ValidationException;
import com.ims.warehouse.domain.model.Warehouse;
import com.ims.warehouse.domain.model.WarehouseStatus;
import com.ims.warehouse.domain.port.in.WarehouseUseCase;
import com.ims.warehouse.domain.port.out.WarehouseEventPublisher;
import com.ims.warehouse.domain.port.out.WarehouseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class WarehouseService implements WarehouseUseCase {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseEventPublisher eventPublisher;

    public WarehouseService(WarehouseRepository warehouseRepository,
                            WarehouseEventPublisher eventPublisher) {
        this.warehouseRepository = warehouseRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Warehouse createWarehouse(String name, String address, UUID userId) {
        Warehouse warehouse = Warehouse.builder()
                .name(name)
                .address(address)
                .userId(userId)
                .status(WarehouseStatus.ACTIVE)
                .build();

        Warehouse saved = warehouseRepository.save(warehouse);

        Map<String, Object> payload = new HashMap<>();
        payload.put("warehouseId", saved.getWarehouseId().toString());
        payload.put("name", saved.getName());
        payload.put("userId", userId.toString());

        eventPublisher.publish(EventEnvelope.of("WAREHOUSE_CREATED", "ims-warehouse-service", userId, payload));
        return saved;
    }

    @Override
    public Warehouse updateWarehouse(UUID warehouseId, String name, String address, UUID requestingUserId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", warehouseId));

        if (name != null) warehouse.setName(name);
        if (address != null) warehouse.setAddress(address);

        Warehouse saved = warehouseRepository.save(warehouse);

        Map<String, Object> payload = new HashMap<>();
        payload.put("warehouseId", saved.getWarehouseId().toString());
        payload.put("name", saved.getName());

        eventPublisher.publish(EventEnvelope.of("WAREHOUSE_UPDATED", "ims-warehouse-service", requestingUserId, payload));
        return saved;
    }

    @Override
    public void deleteWarehouse(UUID warehouseId, UUID requestingUserId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", warehouseId));

        warehouse.setStatus(WarehouseStatus.ARCHIVED);
        warehouseRepository.save(warehouse);

        Map<String, Object> payload = new HashMap<>();
        payload.put("warehouseId", warehouseId.toString());

        eventPublisher.publish(EventEnvelope.of("WAREHOUSE_DELETED", "ims-warehouse-service", requestingUserId, payload));
    }

    @Override
    @Transactional(readOnly = true)
    public Warehouse getWarehouseById(UUID warehouseId) {
        return warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", warehouseId));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Warehouse> listWarehouses(UUID userId, int page, int size) {
        Page<Warehouse> result = userId != null
                ? warehouseRepository.findByUserId(userId, PageRequest.of(page, size))
                : warehouseRepository.findAll(PageRequest.of(page, size));
        return PageResponse.of(result.getContent(), page, size, result.getTotalElements());
    }
}
