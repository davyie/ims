package com.ims.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record SetPriceCommand(
    UUID userId,
    UUID marketId,
    UUID itemId,
    BigDecimal price,
    String currency
) {}
