package com.example.market.commands;

import com.example.common.clients.WarehouseClient;
import com.example.market.domain.Market;
import com.example.market.domain.MarketItem;
import com.example.market.domain.MarketItemQuantity;
import com.example.market.repository.MarketRepository;
import com.example.warehouse.domain.WarehouseItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class AddMarketItemToMarketCommand {
    private MarketRepository marketRepository;
    private WarehouseClient warehouseClient;
    private ObjectMapper objectMapper;

    private final Logger logger = LoggerFactory.getLogger(AddMarketItemToMarketCommand.class);

    @Autowired
    public AddMarketItemToMarketCommand(MarketRepository marketRepository,
                                        WarehouseClient warehouseClient,
                                        ObjectMapper objectMapper) {
        this.marketRepository = marketRepository;
        this.warehouseClient = warehouseClient;
        this.objectMapper = objectMapper;
    }

    public MarketItem execute(String warehouseId, String marketName, Integer itemId, Integer quantity) {
        if (marketRepository.findByName(marketName) == null) {return null;}
        Market market = marketRepository.findByName(marketName).orElseThrow();
        String response = warehouseClient.getItemByItemId(warehouseId, itemId);
        List<MarketItemQuantity> list = market.getItems();
        try {
            warehouseClient.decrementWarehouseItem(warehouseId, itemId, quantity);

            WarehouseItem warehouseItem = objectMapper.readValue(response, WarehouseItem.class);
            MarketItem item = new MarketItem();
            item.setItemId(warehouseItem.getItemId());
            item.setPrice(new Random().nextFloat());
            item.setName(warehouseItem.getName());

            MarketItemQuantity miq = new MarketItemQuantity();
            miq.setItem(item);
            miq.setQuantity(quantity);

            list.add(miq);
            market.setItems(list);
            marketRepository.save(market);

            // Decrement with quantity

            return item;
        } catch (Exception e) {
            return null;
        }
    }
}
