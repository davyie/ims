package com.example.market.routes;

import com.example.market.domain.MarketDTO;
import com.example.market.queries.GetAllMarketsQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/market/query")
public class MarketQueryRoutes {

    private GetAllMarketsQuery getAllMarketsQuery;

    @Autowired
    public MarketQueryRoutes(GetAllMarketsQuery getAllMarketsQuery) {
        this.getAllMarketsQuery = getAllMarketsQuery;
    }

    @GetMapping("/get/all")
    public ResponseEntity<List<MarketDTO>> getAllMarkets() {
        return new ResponseEntity<>(getAllMarketsQuery.execute(), HttpStatus.ACCEPTED);
    }

    @GetMapping("/")
    public ResponseEntity<String> helloWorld() {
        return new ResponseEntity<>("Hello from QueryRoutes", HttpStatus.ACCEPTED);
    }
}
