package com.ims.warehouse.domain.port.out;

import com.ims.warehouse.domain.model.WarehouseStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface StockRepository {

    WarehouseStock save(WarehouseStock stock);

    Optional<WarehouseStock> findByWarehouseIdAndItemId(UUID warehouseId, UUID itemId);

    Page<WarehouseStock> findByWarehouseId(UUID warehouseId, Pageable pageable);
}
