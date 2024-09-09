package project.Domain.Market;

import lombok.Getter;
import lombok.Setter;
import project.Domain.Product.Product;

public class MarketItem {
    @Getter
    @Setter
    private String productId;
    @Getter
    @Setter
    private Long price;

    public MarketItem(){}

    public MarketItem(String productItem, Long price) {
        this.productId = productItem;
        this.price = price;
    }

    public MarketItem(Product product) {
        this.productId = product.getProductId();
        this.price = product.getPrice();
    }
}
