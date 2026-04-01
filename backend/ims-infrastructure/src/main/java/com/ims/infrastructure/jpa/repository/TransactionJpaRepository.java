package com.ims.infrastructure.jpa.repository;

import com.ims.infrastructure.jpa.entity.TransactionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionJpaRepository extends JpaRepository<TransactionJpaEntity, UUID> {
    List<TransactionJpaEntity> findByUserId(UUID userId);
    List<TransactionJpaEntity> findByMarketId(UUID marketId);
    List<TransactionJpaEntity> findByItemId(UUID itemId);
    List<TransactionJpaEntity> findByMarketIdAndItemId(UUID marketId, UUID itemId);
    void deleteByItemId(UUID itemId);
}
