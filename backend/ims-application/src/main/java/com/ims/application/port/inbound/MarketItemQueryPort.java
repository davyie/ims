package com.ims.application.port.inbound;

import com.ims.domain.model.MarketItem;

import java.util.List;
import java.util.UUID;

public interface MarketItemQueryPort {
    List<MarketItem> getMarketItems(UUID marketId);
    MarketItem getMarketItem(UUID marketId, UUID itemId);
}
