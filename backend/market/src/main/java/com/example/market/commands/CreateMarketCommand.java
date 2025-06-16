package com.example.market.commands;

import com.example.market.repository.MarketRepository;
import com.example.market.domain.Market;
import com.example.market.domain.MarketDTO;
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
        market.setName(marketDTO.getName());
        market.setPrice(marketDTO.getPrice());
        market.setItems(marketDTO.getItems());
        marketRepository.save(market); // Save into db
        return marketDTO;
    }
}
