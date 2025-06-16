package com.example.commands;

import com.example.repository.MarketRepository;
import com.example.domain.Market;
import com.example.domain.MarketDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateMarketCommand {
    private MarketRepository marketRepository;

    @Autowired
    public CreateMarketCommand(MarketRepository marketRepository) {
        this.marketRepository = marketRepository;
    }

    public MarketDTO execute(MarketDTO marketDTO) {
        Market market = new Market();
        market.setName(market.getName());
        market.setPrice(market.getPrice());
        market.setItems(market.getItems());
        marketRepository.save(market); // Save into db
        return marketDTO;
    }
}
