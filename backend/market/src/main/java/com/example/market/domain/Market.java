package com.example.market.domain;

import com.example.common.domain.ItemQuantity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document("markets")
public class Market {
    @Id
    private String id;
    private String name;
    private Float price;
    private List<MarketItemQuantity> items;

}
