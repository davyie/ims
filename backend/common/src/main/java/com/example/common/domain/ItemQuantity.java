package com.example.common.domain;

import com.example.common.domain.Item;
import com.example.common.types.ItemType;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemQuantity {
    private ItemType type;
    private Object item;
    private Integer quantity;

    public Integer decrementQuantity(Integer value) {
        return this.quantity -= value;
    }

    public Integer incrementQuantity(Integer value) {
        return this.quantity += value;
    }
}
