package com.ims.api.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record MarketItemSummaryResponse(
    UUID itemId,
    String itemName,
    String sku,
    int allocatedStock,
    int currentStock,
    int sold,
    BigDecimal revenue,
    String currency
) {}
