package com.ims.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "Request to decrement market item stock (record a sale)")
public record DecrementStockRequest(
    @NotNull @Min(1) Integer quantity,
    String note,
    String createdBy,
    @Min(0) BigDecimal salePrice,
    String saleCurrency
) {}
