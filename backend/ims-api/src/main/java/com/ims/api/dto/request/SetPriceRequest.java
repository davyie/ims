package com.ims.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "Request to set market item price")
public record SetPriceRequest(
    @NotNull @DecimalMin("0.0") BigDecimal price,
    @NotBlank @Size(min=3, max=3) String currency
) {}
