package project.Domain.Product;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.UUID;

public class Product {

    @Getter(AccessLevel.PUBLIC) private String productId;
    @Getter(AccessLevel.PUBLIC) private Long price;
}
