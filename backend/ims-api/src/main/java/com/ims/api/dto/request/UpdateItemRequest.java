package com.ims.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "Request to update an existing item")
public record UpdateItemRequest(
    @NotBlank String name,
    String description,
    String category,
    @NotNull @DecimalMin("0.0") BigDecimal defaultPrice,
    @NotBlank @Size(min=3, max=3) String currency,
    @NotBlank String zone,
    @NotBlank String shelf,
    @Min(0) int row,
    @Min(0) int column
) {}
