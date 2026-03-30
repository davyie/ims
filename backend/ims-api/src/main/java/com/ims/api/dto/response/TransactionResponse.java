package com.ims.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Transaction record")
public record TransactionResponse(
    UUID id,
    UUID marketId,
    UUID itemId,
    String type,
    int quantityDelta,
    int stockBefore,
    int stockAfter,
    String note,
    LocalDateTime occurredAt,
    String createdBy
) {}
