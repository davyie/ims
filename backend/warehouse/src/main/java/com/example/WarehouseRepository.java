package com.example;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarehouseRepository extends MongoRepository<WarehouseItem, String> {
    Optional<WarehouseItem> findByItemId(Integer itemId);
    Optional<WarehouseItem> deleteByItemId(Integer itemId);
    Optional<WarehouseItem> findByName(String name);
    Optional<WarehouseItem> deleteByName(String name);
}
