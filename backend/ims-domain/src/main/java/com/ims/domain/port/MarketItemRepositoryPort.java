package com.ims.domain.port;

import com.ims.domain.model.MarketItem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MarketItemRepositoryPort {
    MarketItem save(MarketItem marketItem);
    Optional<MarketItem> findByMarketIdAndItemId(UUID marketId, UUID itemId);
    List<MarketItem> findAllByMarketId(UUID marketId);
    void deleteByItemId(UUID itemId);
}
