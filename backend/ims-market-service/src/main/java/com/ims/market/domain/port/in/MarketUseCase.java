package com.ims.market.domain.port.in;

import com.ims.common.dto.PageResponse;
import com.ims.market.domain.model.Market;
import com.ims.market.domain.model.MarketType;

import java.util.UUID;

public interface MarketUseCase {

    Market createMarket(String name, String location, MarketType marketType, String description, UUID userId);

    Market updateMarket(UUID marketId, String name, String location, String description, UUID requestingUserId);

    void deleteMarket(UUID marketId, UUID requestingUserId);

    Market getMarketById(UUID marketId);

    PageResponse<Market> listMarkets(UUID userId, int page, int size);

    Market openMarket(UUID marketId, UUID requestingUserId);

    Market closeMarket(UUID marketId, UUID requestingUserId);
}
