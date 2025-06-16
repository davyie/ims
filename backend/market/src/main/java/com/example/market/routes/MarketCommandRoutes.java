package com.example.market.routes;

import com.example.market.commands.AddMarketItemToMarketCommand;
import com.example.market.commands.CreateMarketCommand;
import com.example.market.commands.DecrementMarketItemQuantityCommand;
import com.example.market.commands.DeleteMarketItemCommand;
import com.example.market.domain.Market;
import com.example.market.domain.MarketDTO;
import com.example.market.domain.MarketItem;
import com.example.market.domain.MarketItemQuantityDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/market/command")
public class MarketCommandRoutes {

    private CreateMarketCommand cmc;
    private AddMarketItemToMarketCommand amimc;
    private DeleteMarketItemCommand dmic;
    private DecrementMarketItemQuantityCommand dmiqc;

    @Autowired
    public MarketCommandRoutes(CreateMarketCommand cmc,
                               AddMarketItemToMarketCommand addMarketItemToMarketCommand,
                               DeleteMarketItemCommand dmic,
                               DecrementMarketItemQuantityCommand dmiqc) {
        this.cmc = cmc;
        this.amimc = addMarketItemToMarketCommand;
        this.dmic = dmic;
        this.dmiqc = dmiqc;
    }

    @PostMapping("/create")
    public ResponseEntity<MarketDTO> createMarket(@RequestBody MarketDTO marketDTO) {
        return new ResponseEntity(cmc.execute(marketDTO), HttpStatus.ACCEPTED);
    }

    @PostMapping("/add/item")
    public ResponseEntity<MarketItem> addItem(@RequestBody MarketItemQuantityDTO dto) {
        MarketItem item = amimc.execute(dto.getWarehouseId(), dto.getMarketName(), dto.getItemId(), dto.getQuantity());
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    @DeleteMapping("/delete/item")
    public ResponseEntity<Market> deleteItems(@RequestBody MarketItemQuantityDTO dto) {
        return new ResponseEntity<>(this.dmic.execute(dto.getWarehouseId(), dto.getMarketName(), dto.getItemId()), HttpStatus.OK);
    }

    @PutMapping("/decrement/item")
    public ResponseEntity<Market> decrementItem(@RequestBody MarketItemQuantityDTO dto) {
        return new ResponseEntity<>(this.dmiqc.execute(dto.getMarketName(), dto.getItemId(), dto.getQuantity()), HttpStatus.ACCEPTED);
    }
}
