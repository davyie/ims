package com.ims.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Market item details")
public record MarketItemResponse(
    UUID id,
    UUID marketId,
    UUID itemId,
    int allocatedStock,
    int currentStock,
    BigDecimal marketPrice,
    String currency
) {}
