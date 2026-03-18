package com.ims.application.port.inbound;

import com.ims.application.command.DecrementStockCommand;
import com.ims.application.command.IncrementStockCommand;
import com.ims.application.command.SetPriceCommand;
import com.ims.application.command.ShiftItemCommand;
import com.ims.domain.model.MarketItem;

public interface MarketStockCommandPort {
    MarketItem shiftItem(ShiftItemCommand command);
    MarketItem incrementStock(IncrementStockCommand command);
    MarketItem decrementStock(DecrementStockCommand command);
    MarketItem setPrice(SetPriceCommand command);
}
