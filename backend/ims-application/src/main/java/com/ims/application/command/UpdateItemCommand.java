package com.ims.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateItemCommand(
    UUID userId,
    UUID itemId,
    String name,
    String description,
    String category,
    BigDecimal defaultPrice,
    String currency,
    String zone,
    String shelf,
    int row,
    int column
) {}
