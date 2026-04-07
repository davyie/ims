package com.ims.market.domain.port.out;

import com.ims.market.domain.model.MarketStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface MarketStockRepository {

    MarketStock save(MarketStock stock);

    Optional<MarketStock> findByMarketIdAndItemId(UUID marketId, UUID itemId);

    Page<MarketStock> findByMarketId(UUID marketId, Pageable pageable);
}
