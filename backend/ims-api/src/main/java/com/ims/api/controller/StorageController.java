package com.ims.api.controller;

import com.ims.api.dto.response.StorageItemResponse;
import com.ims.api.dto.response.StorageSummaryResponse;
import com.ims.application.dto.StorageItemDto;
import com.ims.application.port.inbound.StorageSummaryPort;
import com.ims.application.query.GetStorageSummaryQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/storage")
@Tag(name = "Storage", description = "Storage summary endpoints")
public class StorageController {

    private final StorageSummaryPort storageSummaryPort;

    public StorageController(StorageSummaryPort storageSummaryPort) {
        this.storageSummaryPort = storageSummaryPort;
    }

    @GetMapping("/summary")
    @Operation(summary = "Get storage summary for all items")
    public ResponseEntity<StorageSummaryResponse> getStorageSummary() {
        List<StorageItemResponse> items = storageSummaryPort.getStorageSummary(new GetStorageSummaryQuery())
                .items().stream()
                .map(i -> new StorageItemResponse(i.itemId(), i.sku(), i.name(), i.category(), i.currentStock()))
                .toList();
        return ResponseEntity.ok(new StorageSummaryResponse(items));
    }
}
