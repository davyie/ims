package com.ims.warehouse.adapter.out.persistence;

import com.ims.warehouse.domain.model.Item;
import com.ims.warehouse.domain.port.out.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class ItemRepositoryAdapter implements ItemRepository {

    private final ItemJpaRepository jpaRepository;

    public ItemRepositoryAdapter(ItemJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Item save(Item item) {
        return jpaRepository.save(item);
    }

    @Override
    public Optional<Item> findById(UUID itemId) {
        return jpaRepository.findById(itemId);
    }

    @Override
    public Page<Item> findByUserId(UUID userId, Pageable pageable) {
        return jpaRepository.findByUserId(userId, pageable);
    }

    @Override
    public boolean existsByUserIdAndSku(UUID userId, String sku) {
        return jpaRepository.existsByUserIdAndSku(userId, sku);
    }

    @Override
    public void delete(Item item) {
        jpaRepository.delete(item);
    }
}
