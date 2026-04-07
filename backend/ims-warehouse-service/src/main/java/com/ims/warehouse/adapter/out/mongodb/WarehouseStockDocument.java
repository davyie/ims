package com.ims.warehouse.adapter.out.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document(collection = "warehouse_stock_snapshots")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseStockDocument {

    @Id
    private String id;

    @Indexed
    private UUID warehouseId;

    @Indexed
    private UUID itemId;

    private String itemSku;
    private String itemName;
    private int quantity;
    private int reservedQty;
    private String binLocation;
    private int reorderLevel;
    private Instant lastUpdated;
    private Instant snapshotAt;
}
