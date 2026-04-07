package com.ims.warehouse.adapter.out.persistence;

import com.ims.warehouse.domain.model.Warehouse;
import com.ims.warehouse.domain.port.out.WarehouseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class WarehouseRepositoryAdapter implements WarehouseRepository {

    private final WarehouseJpaRepository jpaRepository;

    public WarehouseRepositoryAdapter(WarehouseJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Warehouse save(Warehouse warehouse) {
        return jpaRepository.save(warehouse);
    }

    @Override
    public Optional<Warehouse> findById(UUID warehouseId) {
        return jpaRepository.findById(warehouseId);
    }

    @Override
    public Page<Warehouse> findByUserId(UUID userId, Pageable pageable) {
        return jpaRepository.findByUserId(userId, pageable);
    }

    @Override
    public Page<Warehouse> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public void delete(Warehouse warehouse) {
        jpaRepository.delete(warehouse);
    }
}
