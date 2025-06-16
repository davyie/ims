package com.example.warehouse.domain;

import lombok.*;

@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class ChangeQuantity {
    private Integer itemId;
    private Integer quantity;
}
