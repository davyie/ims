package com.example;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class WarehouseItem {
    @Id
    private String id;
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
