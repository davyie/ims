package com.ims.application.port.inbound;

import com.ims.application.command.CloseMarketCommand;
import com.ims.application.command.CreateMarketCommand;
import com.ims.application.command.OpenMarketCommand;
import com.ims.domain.model.Market;

public interface MarketCommandPort {
    Market createMarket(CreateMarketCommand command);
    Market openMarket(OpenMarketCommand command);
    Market closeMarket(CloseMarketCommand command);
}
