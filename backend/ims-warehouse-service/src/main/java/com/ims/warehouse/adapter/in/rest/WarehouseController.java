package com.ims.warehouse.adapter.in.rest;

import com.ims.common.dto.PageResponse;
import com.ims.warehouse.domain.model.Warehouse;
import com.ims.warehouse.domain.model.WarehouseStock;
import com.ims.warehouse.domain.port.in.StockUseCase;
import com.ims.warehouse.domain.port.in.WarehouseUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warehouses")
public class WarehouseController {

    private final WarehouseUseCase warehouseUseCase;
    private final StockUseCase stockUseCase;

    public WarehouseController(WarehouseUseCase warehouseUseCase, StockUseCase stockUseCase) {
        this.warehouseUseCase = warehouseUseCase;
        this.stockUseCase = stockUseCase;
    }

    record CreateWarehouseRequest(@NotBlank String name, String address) {}
    record UpdateWarehouseRequest(String name, String address) {}
    record AddStockRequest(@NotNull UUID itemId, @Positive int quantity, String binLocation) {}
    record RemoveStockRequest(@NotNull UUID itemId, @Positive int quantity) {}
    record AdjustStockRequest(@NotNull UUID itemId, @PositiveOrZero int newQuantity) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Warehouse createWarehouse(@Valid @RequestBody CreateWarehouseRequest request,
                                     Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        return warehouseUseCase.createWarehouse(request.name(), request.address(), userId);
    }

    @GetMapping
    public PageResponse<Warehouse> listWarehouses(
            @RequestParam(required = false) UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {
        UUID requestingUserId = UUID.fromString(auth.getName());
        return warehouseUseCase.listWarehouses(userId != null ? userId : requestingUserId, page, size);
    }

    @GetMapping("/{warehouseId}")
    public Warehouse getWarehouse(@PathVariable UUID warehouseId) {
        return warehouseUseCase.getWarehouseById(warehouseId);
    }

    @PutMapping("/{warehouseId}")
    public Warehouse updateWarehouse(@PathVariable UUID warehouseId,
                                     @Valid @RequestBody UpdateWarehouseRequest request,
                                     Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        return warehouseUseCase.updateWarehouse(warehouseId, request.name(), request.address(), userId);
    }

    @DeleteMapping("/{warehouseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWarehouse(@PathVariable UUID warehouseId, Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        warehouseUseCase.deleteWarehouse(warehouseId, userId);
    }

    @GetMapping("/{warehouseId}/stock")
    public PageResponse<WarehouseStock> listStock(
            @PathVariable UUID warehouseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return stockUseCase.listStockByWarehouse(warehouseId, page, size);
    }

    @PostMapping("/{warehouseId}/stock/add")
    public WarehouseStock addStock(@PathVariable UUID warehouseId,
                                   @Valid @RequestBody AddStockRequest request) {
        return stockUseCase.addStock(warehouseId, request.itemId(), request.quantity(), request.binLocation());
    }

    @PostMapping("/{warehouseId}/stock/remove")
    public WarehouseStock removeStock(@PathVariable UUID warehouseId,
                                      @Valid @RequestBody RemoveStockRequest request) {
        return stockUseCase.removeStock(warehouseId, request.itemId(), request.quantity());
    }

    @PutMapping("/{warehouseId}/stock/adjust")
    public WarehouseStock adjustStock(@PathVariable UUID warehouseId,
                                      @Valid @RequestBody AdjustStockRequest request) {
        return stockUseCase.adjustStock(warehouseId, request.itemId(), request.newQuantity());
    }
}
