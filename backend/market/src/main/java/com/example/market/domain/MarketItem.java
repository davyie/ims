package com.example.market.domain;

import com.example.common.domain.Item;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Data
@Setter
@Getter
public class MarketItem implements Item {
    @Id
    private String id;
    private Integer itemId;
    private String name;
    private Float price;
}
