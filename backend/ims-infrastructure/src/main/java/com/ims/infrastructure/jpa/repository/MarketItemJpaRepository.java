package com.ims.infrastructure.jpa.repository;

import com.ims.infrastructure.jpa.entity.MarketItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MarketItemJpaRepository extends JpaRepository<MarketItemJpaEntity, UUID> {
    Optional<MarketItemJpaEntity> findByMarketIdAndItemId(UUID marketId, UUID itemId);
    List<MarketItemJpaEntity> findAllByMarketId(UUID marketId);
    void deleteByItemId(UUID itemId);
}
