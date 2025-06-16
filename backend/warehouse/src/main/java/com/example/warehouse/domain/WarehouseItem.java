package com.example.warehouse.domain;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarehouseItem {
    private String name;
    private String description;
    private Integer itemId;
    private static Integer itemIdGenerator = 0;

    public WarehouseItem(String name, String description) {
        this.name = name;
        this.description = description;
        this.itemId = itemIdGenerator;
        itemIdGenerator++;
    }
}
