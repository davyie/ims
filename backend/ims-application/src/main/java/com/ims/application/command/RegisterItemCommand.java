package com.ims.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record RegisterItemCommand(
    UUID userId,
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
    int initialStock
) {}
