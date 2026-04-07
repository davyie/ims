package com.ims.warehouse.adapter.out.persistence;

import com.ims.warehouse.domain.model.WarehouseStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockJpaRepository extends JpaRepository<WarehouseStock, UUID> {

    Optional<WarehouseStock> findByWarehouseIdAndItemId(UUID warehouseId, UUID itemId);

    Page<WarehouseStock> findByWarehouseId(UUID warehouseId, Pageable pageable);
}
