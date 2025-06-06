package com.example;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@NoArgsConstructor
@Document(collection = "warehouse")
public class Warehouse {
    @Id
    private String id;
    private Map<String, Integer> inventory;
}
