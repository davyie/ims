package com.ims.warehouse.application.service;

import com.ims.common.dto.PageResponse;
import com.ims.common.event.EventEnvelope;
import com.ims.common.exception.ConflictException;
import com.ims.common.exception.ResourceNotFoundException;
import com.ims.warehouse.domain.model.Item;
import com.ims.warehouse.domain.port.in.ItemUseCase;
import com.ims.warehouse.domain.port.out.ItemRepository;
import com.ims.warehouse.domain.port.out.WarehouseEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class ItemService implements ItemUseCase {

    private final ItemRepository itemRepository;
    private final WarehouseEventPublisher eventPublisher;

    public ItemService(ItemRepository itemRepository, WarehouseEventPublisher eventPublisher) {
        this.itemRepository = itemRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Item createItem(String sku, String name, String description, String category,
                           String unitOfMeasure, BigDecimal unitPrice, UUID userId) {
        if (itemRepository.existsByUserIdAndSku(userId, sku)) {
            throw new ConflictException("Item with SKU already exists: " + sku);
        }

        Item item = Item.builder()
                .sku(sku)
                .name(name)
                .description(description)
                .category(category)
                .unitOfMeasure(unitOfMeasure)
                .unitPrice(unitPrice)
                .userId(userId)
                .build();

        Item saved = itemRepository.save(item);

        Map<String, Object> payload = new HashMap<>();
        payload.put("itemId", saved.getItemId().toString());
        payload.put("sku", saved.getSku());
        payload.put("name", saved.getName());
        payload.put("userId", userId.toString());

        eventPublisher.publish(EventEnvelope.of("ITEM_CREATED", "ims-warehouse-service", userId, payload));
        return saved;
    }

    @Override
    public Item updateItem(UUID itemId, String name, String description, String category,
                           String unitOfMeasure, BigDecimal unitPrice, UUID requestingUserId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item", itemId));

        if (name != null) item.setName(name);
        if (description != null) item.setDescription(description);
        if (category != null) item.setCategory(category);
        if (unitOfMeasure != null) item.setUnitOfMeasure(unitOfMeasure);
        if (unitPrice != null) item.setUnitPrice(unitPrice);

        Item saved = itemRepository.save(item);

        Map<String, Object> payload = new HashMap<>();
        payload.put("itemId", saved.getItemId().toString());
        payload.put("name", saved.getName());

        eventPublisher.publish(EventEnvelope.of("ITEM_UPDATED", "ims-warehouse-service", requestingUserId, payload));
        return saved;
    }

    @Override
    public void deleteItem(UUID itemId, UUID requestingUserId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item", itemId));

        itemRepository.delete(item);

        Map<String, Object> payload = new HashMap<>();
        payload.put("itemId", itemId.toString());

        eventPublisher.publish(EventEnvelope.of("ITEM_DELETED", "ims-warehouse-service", requestingUserId, payload));
    }

    @Override
    @Transactional(readOnly = true)
    public Item getItemById(UUID itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item", itemId));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Item> listItems(UUID userId, int page, int size) {
        Page<Item> result = itemRepository.findByUserId(userId, PageRequest.of(page, size));
        return PageResponse.of(result.getContent(), page, size, result.getTotalElements());
    }
}
