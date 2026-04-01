package com.ims.infrastructure.jpa.repository;

import com.ims.infrastructure.jpa.entity.ItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemJpaRepository extends JpaRepository<ItemJpaEntity, UUID> {
    Optional<ItemJpaEntity> findBySkuAndUserId(String sku, UUID userId);
    List<ItemJpaEntity> findAllByUserId(UUID userId);
}
