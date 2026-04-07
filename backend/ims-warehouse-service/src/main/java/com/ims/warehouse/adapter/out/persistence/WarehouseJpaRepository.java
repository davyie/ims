package com.ims.warehouse.adapter.out.persistence;

import com.ims.warehouse.domain.model.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WarehouseJpaRepository extends JpaRepository<Warehouse, UUID> {

    Page<Warehouse> findByUserId(UUID userId, Pageable pageable);
}
