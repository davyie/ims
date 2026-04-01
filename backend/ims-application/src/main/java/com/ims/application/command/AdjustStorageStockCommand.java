package com.ims.application.command;

import java.util.UUID;

public record AdjustStorageStockCommand(
    UUID userId,
    UUID itemId,
    int delta,
    String note,
    String createdBy
) {}
