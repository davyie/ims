package com.example.market.queries;

import com.example.market.repository.MarketRepository;
import com.example.market.domain.MarketDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetAllMarketsQuery {
    private MarketRepository marketRepository;

    @Autowired
    public GetAllMarketsQuery(MarketRepository marketRepository) {
        this.marketRepository = marketRepository;
    }

    public List<MarketDTO> execute() {
        return marketRepository
                .findAll()
                .stream()
                .map(m -> new MarketDTO(
                        m.getName(),
                        m.getPrice(),
                        m.getItems())).toList();
    }
}
