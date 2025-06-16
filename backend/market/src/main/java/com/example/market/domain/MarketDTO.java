package com.example.market.domain;

import com.example.common.domain.ItemQuantity;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class MarketDTO {
    private String name;
    private Float price;
    private List<MarketItemQuantity> items;
}
