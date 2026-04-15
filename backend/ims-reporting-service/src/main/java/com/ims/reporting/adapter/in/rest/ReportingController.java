package com.ims.reporting.adapter.in.rest;

import com.ims.common.dto.PageResponse;
import com.ims.reporting.application.service.ReportingService;
import com.ims.reporting.domain.model.EventProjectionDocument;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportingController {

    private final ReportingService reportingService;

    public ReportingController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @GetMapping("/warehouse/{warehouseId}")
    public PageResponse<EventProjectionDocument> getWarehouseReport(
            @PathVariable UUID warehouseId,
            @RequestParam(defaultValue = "INVENTORY_SNAPSHOT") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return switch (type) {
            case "MOVEMENT_HISTORY" -> reportingService.getWarehouseMovementHistory(warehouseId, page, size);
            case "LOW_STOCK" -> reportingService.getLowStockReport(warehouseId, page, size);
            case "VALUATION" -> reportingService.getWarehouseValuation(warehouseId, page, size);
            default -> reportingService.getWarehouseInventorySnapshot(warehouseId, page, size);
        };
    }

    @GetMapping("/market/{marketId}")
    public PageResponse<EventProjectionDocument> getMarketReport(
            @PathVariable UUID marketId,
            @RequestParam(defaultValue = "SESSION") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return switch (type) {
            case "STOCK_COMPARISON" -> reportingService.getMarketStockComparison(marketId, page, size);
            default -> reportingService.getMarketSessionReport(marketId, page, size);
        };
    }

    @GetMapping("/markets/sales")
    public PageResponse<EventProjectionDocument> getMarketSalesReport(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size) {
        return reportingService.getAllMarketSalesEvents(page, size);
    }

    @GetMapping("/transfers")
    public PageResponse<EventProjectionDocument> getTransferReport(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return reportingService.getTransferHistoryReport(page, size);
    }
}
