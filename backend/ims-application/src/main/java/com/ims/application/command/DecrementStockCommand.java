package com.ims.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record DecrementStockCommand(
    UUID userId,
    UUID marketId,
    UUID itemId,
    int quantity,
    String note,
    String createdBy,
    BigDecimal salePrice,
    String saleCurrency
) {}
