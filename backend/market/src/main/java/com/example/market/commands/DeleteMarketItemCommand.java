package com.example.market.commands;

import com.example.common.domain.Item;
import com.example.common.domain.ItemQuantity;
import com.example.market.domain.Market;
import com.example.market.domain.MarketItem;
import com.example.market.repository.MarketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeleteMarketItemCommand {

    private final Logger logger = LoggerFactory.getLogger(DeleteMarketItemCommand.class);
    private MarketRepository marketRepository;

    @Autowired
    public DeleteMarketItemCommand(MarketRepository marketRepository) {
        this.marketRepository = marketRepository;
    }

    public Market execute(String marketName, Integer itemId, Integer quantity) {
//        Market m = marketRepository.findByName(marketName).orElseThrow();
//        List<ItemQuantity> list = m.getItems()
//                .stream()
//                .filter(iq -> {
//            MarketItem i = (MarketItem) iq.getItem();
//            return !i.getName().equals(itemName);
//        }).toList();
//        m.setItems(list);
//        marketRepository.save(m);
//        // Add back to warehouse as well.
//        return m;
        return null;
    }
}