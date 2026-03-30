package com.ims.application.query;

import java.util.UUID;

public record GetTransactionHistoryQuery(
    UUID marketId,
    UUID itemId,
    int page,
    int size
) {}
