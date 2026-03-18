package com.ims.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to adjust storage stock")
public record AdjustStockRequest(
    @NotNull @Schema(description = "Delta (positive = add, negative = remove)", example = "10") Integer delta,
    @Schema(description = "Reason for adjustment") String note,
    @Schema(description = "Who is making the adjustment", example = "system") String createdBy
) {}
