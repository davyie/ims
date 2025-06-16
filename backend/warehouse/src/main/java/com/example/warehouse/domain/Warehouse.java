package com.example.warehouse.domain;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "warehouse")
public class Warehouse {
    @Id
    private String id;
    private List<WarehouseItemQuantity> inventory;
}
