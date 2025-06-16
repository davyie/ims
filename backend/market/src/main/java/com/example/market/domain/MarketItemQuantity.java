package com.example.market.domain;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MarketItemQuantity {
    private MarketItem item;
    private Integer quantity;
}
