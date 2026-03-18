package com.ims.application.command;

import java.util.UUID;

public record DecrementStockCommand(
    UUID marketId,
    UUID itemId,
    int quantity,
    String note,
    String createdBy
) {}
