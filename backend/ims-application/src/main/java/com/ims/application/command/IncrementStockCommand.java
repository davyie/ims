package com.ims.application.command;

import java.util.UUID;

public record IncrementStockCommand(
    UUID marketId,
    UUID itemId,
    int quantity,
    String note,
    String createdBy
) {}
