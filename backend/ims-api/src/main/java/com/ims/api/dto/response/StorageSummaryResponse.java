package com.ims.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Storage summary for all items")
public record StorageSummaryResponse(List<StorageItemResponse> items) {}
