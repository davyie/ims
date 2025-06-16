package com.example.warehouse.domain;

import lombok.*;

@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class DecrementQuantity {
    private Integer itemId;
    private Integer quantity;
}
