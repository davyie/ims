package com.ims.market.domain.port.in;

import com.ims.common.dto.PageResponse;
import com.ims.market.domain.model.MarketStock;

import java.util.UUID;

public interface MarketStockUseCase {

    MarketStock incrementStock(UUID marketId, UUID itemId, int quantity);

    MarketStock decrementStock(UUID marketId, UUID itemId, int quantity);

    MarketStock receiveStock(UUID marketId, UUID itemId, int quantity);

    MarketStock getStock(UUID marketId, UUID itemId);

    PageResponse<MarketStock> listStockByMarket(UUID marketId, int page, int size);
}
