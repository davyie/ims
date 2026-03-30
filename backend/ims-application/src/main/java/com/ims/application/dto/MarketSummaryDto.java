package com.ims.application.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record MarketSummaryDto(
    UUID marketId,
    String marketName,
    int totalItemTypes,
    int totalAllocatedStock,
    int totalCurrentStock,
    int totalSold,
    BigDecimal totalRevenue,
    String currency,
    List<MarketItemSummaryDto> items
) {}
