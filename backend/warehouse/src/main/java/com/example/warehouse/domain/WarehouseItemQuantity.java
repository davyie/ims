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

    public Integer decrementQuantity(Integer value) throws IllegalStateException{
        if (this.quantity - value < 0) {throw new IllegalStateException("Quantity is below 0");}
        return this.quantity -= value;
    }

    public Integer incrementQuantity(Integer value) {
        return this.quantity += value;
    }
}
