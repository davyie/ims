package com.example.warehouse.domain;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseItemQuantity {
    private WarehouseItem item;
    private Integer quantity;

    public Integer decrementQuantity(Integer value) {
        return this.quantity -= value;
    }

    public Integer incrementQuantity(Integer value) {
        return this.quantity += value;
    }
}
