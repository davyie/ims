package com.ims.application.port.inbound;

import com.ims.application.command.CloseMarketCommand;
import com.ims.application.command.CreateMarketCommand;
import com.ims.application.command.OpenMarketCommand;
import com.ims.application.command.UpdateMarketCommand;
import com.ims.domain.model.Market;
import java.util.UUID;

public interface MarketCommandPort {
    Market createMarket(CreateMarketCommand command);
    Market openMarket(OpenMarketCommand command);
    Market closeMarket(CloseMarketCommand command);
    Market updateMarket(UpdateMarketCommand command);
    void deleteMarket(UUID id);
}
