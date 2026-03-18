package com.ims.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Schema(description = "Market summary report")
public record MarketSummaryResponse(
    UUID marketId,
    String marketName,
    int totalItemTypes,
    int totalAllocatedStock,
    int totalCurrentStock,
    int totalSold,
    BigDecimal totalRevenue,
    String currency,
    List<MarketItemSummaryResponse> items
) {}
