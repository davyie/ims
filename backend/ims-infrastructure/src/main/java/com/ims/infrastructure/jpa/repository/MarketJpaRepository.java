package com.ims.infrastructure.jpa.repository;

import com.ims.infrastructure.jpa.entity.MarketJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MarketJpaRepository extends JpaRepository<MarketJpaEntity, UUID> {
    List<MarketJpaEntity> findByUserIdAndStatus(UUID userId, MarketJpaEntity.MarketStatusJpa status);
    List<MarketJpaEntity> findByUserId(UUID userId);
}
