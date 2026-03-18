package com.ims.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Request to shift an item to a market")
public record ShiftItemRequest(
    @NotNull UUID itemId,
    @Min(1) int quantity,
    @NotNull @DecimalMin("0.0") BigDecimal marketPrice,
    @NotBlank @Size(min=3, max=3) String currency,
    @Schema(example = "system") String createdBy
) {}
