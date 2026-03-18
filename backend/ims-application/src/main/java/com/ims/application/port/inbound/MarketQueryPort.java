package com.ims.application.port.inbound;

import com.ims.application.dto.AllMarketsSummaryDto;
import com.ims.application.dto.MarketSummaryDto;
import com.ims.application.query.GetAllMarketsSummaryQuery;
import com.ims.application.query.GetMarketQuery;
import com.ims.application.query.GetMarketSummaryQuery;
import com.ims.application.query.ListMarketsQuery;
import com.ims.domain.model.Market;

import java.util.List;

public interface MarketQueryPort {
    Market getMarket(GetMarketQuery query);
    List<Market> listMarkets(ListMarketsQuery query);
    MarketSummaryDto getMarketSummary(GetMarketSummaryQuery query);
    AllMarketsSummaryDto getAllMarketsSummary(GetAllMarketsSummaryQuery query);
}
