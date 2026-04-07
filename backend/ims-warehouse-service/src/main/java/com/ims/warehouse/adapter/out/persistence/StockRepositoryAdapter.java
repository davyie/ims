package com.ims.warehouse.adapter.out.persistence;

import com.ims.warehouse.domain.model.WarehouseStock;
import com.ims.warehouse.domain.port.out.StockRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class StockRepositoryAdapter implements StockRepository {

    private final StockJpaRepository jpaRepository;

    public StockRepositoryAdapter(StockJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public WarehouseStock save(WarehouseStock stock) {
        return jpaRepository.save(stock);
    }

    @Override
    public Optional<WarehouseStock> findByWarehouseIdAndItemId(UUID warehouseId, UUID itemId) {
        return jpaRepository.findByWarehouseIdAndItemId(warehouseId, itemId);
    }

    @Override
    public Page<WarehouseStock> findByWarehouseId(UUID warehouseId, Pageable pageable) {
        return jpaRepository.findByWarehouseId(warehouseId, pageable);
    }
}
