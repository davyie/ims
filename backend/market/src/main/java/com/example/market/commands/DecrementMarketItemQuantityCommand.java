package com.example.market.commands;

import com.example.market.domain.Market;
import com.example.market.domain.MarketItemQuantity;
import com.example.market.repository.MarketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component
public class DecrementMarketItemQuantityCommand {
    private MarketRepository marketRepository;

    @Autowired
    public DecrementMarketItemQuantityCommand(MarketRepository marketRepository) {
        this.marketRepository = marketRepository;
    }

    public Market execute(String marketName, Integer itemId, Integer quantity) {
        Market m = marketRepository.findByName(marketName).orElseThrow();
        List<MarketItemQuantity> list = m.getItems();

        Iterator it = list.iterator();
        while (it.hasNext()) {
            MarketItemQuantity miq = (MarketItemQuantity) it.next();
            if (miq.getItem().getItemId() == itemId) {
                miq.setQuantity(miq.getQuantity() - quantity);
            }
        }

        m.setItems(list);
        marketRepository.save(m);
        return m;
    }
}
