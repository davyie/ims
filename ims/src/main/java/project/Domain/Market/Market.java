package project.Domain.Market;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import project.Domain.Product.Product;

@Document(collection = "markets")
public class Market {

    @Getter
    @Setter
    @Id
    private String id;
    @Getter
    @Setter
    @NonNull
    private MarketStatus marketStatus;
    @Getter
    @Setter
    @NonNull
    private List<MarketItem> marketItems;

    @Getter
    @Setter
    @NonNull
    private Long price;

    public Market(String id) {
        this.id = id;
        this.marketStatus = MarketStatus.OPEN;
        this.marketItems = new ArrayList<>();
        this.price = 0L;
    }

    private boolean validateState() {
        return marketStatus != MarketStatus.CLOSED;
    }

    public void closeMarket() {
        if (!validateState()) return;
        this.marketStatus = MarketStatus.CLOSED;
    }

    public MarketItem addMarket(Product product) {
        if (!validateState()) return null;
        MarketItem mi = new MarketItem(product);
        marketItems.add(mi);
        return mi;
    }
}
