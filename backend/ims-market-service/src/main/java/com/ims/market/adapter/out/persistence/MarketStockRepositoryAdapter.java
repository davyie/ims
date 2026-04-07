package com.ims.market.adapter.out.persistence;

import com.ims.market.domain.model.MarketStock;
import com.ims.market.domain.port.out.MarketStockRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class MarketStockRepositoryAdapter implements MarketStockRepository {

    private final MarketStockJpaRepository jpaRepository;

    public MarketStockRepositoryAdapter(MarketStockJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public MarketStock save(MarketStock stock) {
        return jpaRepository.save(stock);
    }

    @Override
    public Optional<MarketStock> findByMarketIdAndItemId(UUID marketId, UUID itemId) {
        return jpaRepository.findByMarketIdAndItemId(marketId, itemId);
    }

    @Override
    public Page<MarketStock> findByMarketId(UUID marketId, Pageable pageable) {
        return jpaRepository.findByMarketId(marketId, pageable);
    }
}
