package com.ims.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to increment market item stock")
public record IncrementStockRequest(
    @NotNull @Min(1) Integer quantity,
    String note,
    String createdBy
) {}
