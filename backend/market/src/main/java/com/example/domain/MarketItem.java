package com.example;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class MarketItem {
    private String itemId;
    private String name;
    private Float price;
}
