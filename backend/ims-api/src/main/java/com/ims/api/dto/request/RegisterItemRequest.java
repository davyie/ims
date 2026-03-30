package com.ims.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "Request to register a new item")
public record RegisterItemRequest(
    @NotBlank @Schema(description = "Stock Keeping Unit", example = "SKU-001") String sku,
    @NotBlank @Schema(description = "Item name", example = "Wooden Chair") String name,
    @Schema(description = "Item description") String description,
    @Schema(description = "Category", example = "Furniture") String category,
    @NotNull @DecimalMin("0.0") @Schema(description = "Default price", example = "29.99") BigDecimal defaultPrice,
    @NotBlank @Size(min=3, max=3) @Schema(description = "ISO 4217 currency code", example = "EUR") String currency,
    @NotBlank @Schema(description = "Storage zone", example = "A1") String zone,
    @NotBlank @Schema(description = "Storage shelf", example = "S01") String shelf,
    @Min(0) @Schema(description = "Row number", example = "1") int row,
    @Min(0) @Schema(description = "Column number", example = "2") int column,
    @Min(0) @Schema(description = "Initial stock quantity", example = "10") int initialStock
) {}
