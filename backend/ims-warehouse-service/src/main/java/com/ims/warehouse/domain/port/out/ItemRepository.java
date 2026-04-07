package com.ims.warehouse.domain.port.out;

import com.ims.warehouse.domain.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ItemRepository {

    Item save(Item item);

    Optional<Item> findById(UUID itemId);

    Page<Item> findByUserId(UUID userId, Pageable pageable);

    boolean existsByUserIdAndSku(UUID userId, String sku);

    void delete(Item item);
}
