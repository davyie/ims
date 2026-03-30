package com.ims.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Summary across all markets")
public record AllMarketsSummaryResponse(
    int totalMarkets,
    int totalItemsSold,
    BigDecimal totalRevenue,
    String currency,
    List<MarketSummaryResponse> markets
) {}
