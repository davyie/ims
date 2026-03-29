package com.ims.infrastructure.jpa.adapter;

import com.ims.domain.model.Item;
import com.ims.domain.model.MarketStatus;
import com.ims.domain.port.ItemRepositoryPort;
import com.ims.infrastructure.jpa.entity.ItemJpaEntity;
import com.ims.infrastructure.jpa.repository.ItemJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.ims.domain.valueobject.Money;
import com.ims.domain.valueobject.StoragePosition;
import com.ims.domain.valueobject.StockLevel;

@Component
public class ItemRepositoryAdapter implements ItemRepositoryPort {

    private final ItemJpaRepository jpaRepository;

    public ItemRepositoryAdapter(ItemJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Item save(Item item) {
        ItemJpaEntity entity = toEntity(item);
        ItemJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Item> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Item> findBySku(String sku) {
        return jpaRepository.findBySku(sku).map(this::toDomain);
    }

    @Override
    public List<Item> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private ItemJpaEntity toEntity(Item item) {
        ItemJpaEntity e = new ItemJpaEntity();
        e.setId(item.getId());
        e.setSku(item.getSku());
        e.setName(item.getName());
        e.setDescription(item.getDescription());
        e.setCategory(item.getCategory());
        if (item.getDefaultPrice() != null) {
            e.setDefaultPrice(item.getDefaultPrice().amount());
            e.setDefaultCurrency(item.getDefaultPrice().currency());
        }
        if (item.getStoragePosition() != null) {
            e.setStorageZone(item.getStoragePosition().zone());
            e.setStorageShelf(item.getStoragePosition().shelf());
            e.setStorageRow(item.getStoragePosition().row());
            e.setStorageColumn(item.getStoragePosition().column());
        }
        e.setTotalStorageStock(item.getTotalStorageStock().quantity());
        e.setCreatedAt(item.getCreatedAt());
        e.setUpdatedAt(item.getUpdatedAt());
        return e;
    }

    private Item toDomain(ItemJpaEntity e) {
        Money price = (e.getDefaultPrice() != null && e.getDefaultCurrency() != null)
            ? Money.of(e.getDefaultPrice(), e.getDefaultCurrency()) : null;
        StoragePosition pos = (e.getStorageZone() != null)
            ? new StoragePosition(e.getStorageZone(), e.getStorageShelf(), e.getStorageRow(), e.getStorageColumn()) : null;
        return new Item(e.getId(), e.getSku(), e.getName(), e.getDescription(), e.getCategory(),
            price, pos, StockLevel.of(e.getTotalStorageStock()), e.getCreatedAt(), e.getUpdatedAt());
    }
}
