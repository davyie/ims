package com.ims.warehouse.domain.port.in;

import com.ims.common.dto.PageResponse;
import com.ims.warehouse.domain.model.Warehouse;

import java.util.UUID;

public interface WarehouseUseCase {

    Warehouse createWarehouse(String name, String address, UUID userId);

    Warehouse updateWarehouse(UUID warehouseId, String name, String address, UUID requestingUserId);

    void deleteWarehouse(UUID warehouseId, UUID requestingUserId);

    Warehouse getWarehouseById(UUID warehouseId);

    PageResponse<Warehouse> listWarehouses(UUID userId, int page, int size);
}
