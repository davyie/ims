package com.ims.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Item details")
public record ItemResponse(
    UUID id,
    String sku,
    String name,
    String description,
    String category,
    BigDecimal defaultPrice,
    String currency,
    String zone,
    String shelf,
    int row,
    int column,
    int totalStorageStock,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
