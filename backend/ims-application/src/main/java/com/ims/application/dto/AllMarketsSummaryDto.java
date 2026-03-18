package com.ims.application.dto;

import java.math.BigDecimal;
import java.util.List;

public record AllMarketsSummaryDto(
    int totalMarkets,
    int totalItemsSold,
    BigDecimal totalRevenue,
    String currency,
    List<MarketSummaryDto> markets
) {}
