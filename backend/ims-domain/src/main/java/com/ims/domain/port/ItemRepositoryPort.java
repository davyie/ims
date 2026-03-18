package com.ims.domain.port;

import com.ims.domain.model.Item;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemRepositoryPort {
    Item save(Item item);
    Optional<Item> findById(UUID id);
    Optional<Item> findBySku(String sku);
    List<Item> findAll();
}
