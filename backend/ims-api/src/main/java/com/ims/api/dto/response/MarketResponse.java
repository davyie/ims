package com.ims.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Market details")
public record MarketResponse(
    UUID id,
    String name,
    String place,
    LocalDateTime openDate,
    LocalDateTime closeDate,
    String status,
    LocalDateTime createdAt
) {}
