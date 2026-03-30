package com.ims.infrastructure.jpa.repository;

import com.ims.infrastructure.jpa.entity.ItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ItemJpaRepository extends JpaRepository<ItemJpaEntity, UUID> {
    Optional<ItemJpaEntity> findBySku(String sku);
}
