package com.ims.warehouse.domain.port.in;

import com.ims.common.dto.PageResponse;
import com.ims.warehouse.domain.model.WarehouseStock;

import java.util.UUID;

public interface StockUseCase {

    WarehouseStock addStock(UUID warehouseId, UUID itemId, int quantity, String binLocation);

    WarehouseStock removeStock(UUID warehouseId, UUID itemId, int quantity);

    WarehouseStock reserveStock(UUID warehouseId, UUID itemId, int quantity, UUID correlationId);

    WarehouseStock releaseReservation(UUID warehouseId, UUID itemId, int quantity);

    WarehouseStock commitReservation(UUID warehouseId, UUID itemId, int quantity, UUID correlationId);

    WarehouseStock adjustStock(UUID warehouseId, UUID itemId, int newQuantity);

    WarehouseStock getStock(UUID warehouseId, UUID itemId);

    PageResponse<WarehouseStock> listStockByWarehouse(UUID warehouseId, int page, int size);
}
