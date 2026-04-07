package com.ims.warehouse.adapter.out.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockMongoRepository extends MongoRepository<WarehouseStockDocument, String> {

    List<WarehouseStockDocument> findByWarehouseId(UUID warehouseId);

    Optional<WarehouseStockDocument> findByWarehouseIdAndItemId(UUID warehouseId, UUID itemId);
}
