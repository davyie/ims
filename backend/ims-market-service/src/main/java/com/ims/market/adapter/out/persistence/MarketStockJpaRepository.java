package com.ims.market.adapter.out.persistence;

import com.ims.market.domain.model.MarketStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MarketStockJpaRepository extends JpaRepository<MarketStock, UUID> {

    Optional<MarketStock> findByMarketIdAndItemId(UUID marketId, UUID itemId);

    Page<MarketStock> findByMarketId(UUID marketId, Pageable pageable);
}
