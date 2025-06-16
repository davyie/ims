package com.example.market.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class MarketItemQuantityDTO {
    private String warehouseId;
    private String marketName;
    private Integer itemId;
    private Integer quantity;
}
