package com.example.common.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseItemDTO {
    private String name;
    private String description;
    private Integer itemId;

    public WarehouseItemDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
