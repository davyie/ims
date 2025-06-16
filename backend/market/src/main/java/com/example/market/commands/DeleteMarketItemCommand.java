package com.example.market.commands;

import com.example.common.clients.WarehouseClient;
import com.example.common.domain.Item;
import com.example.common.domain.ItemQuantity;
import com.example.market.domain.Market;
import com.example.market.domain.MarketItem;
import com.example.market.domain.MarketItemQuantity;
import com.example.market.repository.MarketRepository;
import com.example.warehouse.domain.ChangeQuantity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component
public class DeleteMarketItemCommand {

    private final Logger logger = LoggerFactory.getLogger(DeleteMarketItemCommand.class);
    private MarketRepository marketRepository;
    private WarehouseClient warehouseClient;

    @Autowired
    public DeleteMarketItemCommand(MarketRepository marketRepository, WarehouseClient warehouseClient) {
        this.marketRepository = marketRepository;
        this.warehouseClient = warehouseClient;
    }

    public Market execute(String warehouseId, String marketName, Integer itemId) {
        if (marketRepository.findByName(marketName).isEmpty()) {return null;}
        Market m = marketRepository.findByName(marketName).orElseThrow();
        List<MarketItemQuantity> list = m.getItems();
        // Find the item
        Iterator it = list.iterator();
        MarketItemQuantity mi = null;
        while (it.hasNext()) {
            MarketItemQuantity i = (MarketItemQuantity) it.next();
            if (i.getItem().getItemId() == itemId) {
                mi = i;
                break;
            }
        }
        // Delete the item
        list = list.stream().filter( miq -> miq.getItem().getItemId() != itemId).toList();
        m.setItems(list);
        marketRepository.save(m);
        // Increment in warehouse
        warehouseClient.incrementWarehouseItem(warehouseId, itemId, mi.getQuantity()); // Increment it
        return m;
    }
}