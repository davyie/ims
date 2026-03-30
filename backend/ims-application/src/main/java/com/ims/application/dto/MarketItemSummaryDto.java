package com.ims.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record MarketItemSummaryDto(
    UUID itemId,
    String itemName,
    String sku,
    int allocatedStock,
    int currentStock,
    int sold,
    BigDecimal revenue,
    String currency
) {}
