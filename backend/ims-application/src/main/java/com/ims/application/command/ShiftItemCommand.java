package com.ims.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record ShiftItemCommand(
    UUID marketId,
    UUID itemId,
    int quantity,
    BigDecimal marketPrice,
    String currency,
    String createdBy
) {}
