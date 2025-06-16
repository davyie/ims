package com.example.domain;

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
