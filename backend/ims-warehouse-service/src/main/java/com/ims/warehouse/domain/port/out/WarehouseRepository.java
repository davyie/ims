package com.ims.warehouse.domain.port.out;

import com.ims.warehouse.domain.model.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface WarehouseRepository {

    Warehouse save(Warehouse warehouse);

    Optional<Warehouse> findById(UUID warehouseId);

    Page<Warehouse> findByUserId(UUID userId, Pageable pageable);

    Page<Warehouse> findAll(Pageable pageable);

    void delete(Warehouse warehouse);
}
