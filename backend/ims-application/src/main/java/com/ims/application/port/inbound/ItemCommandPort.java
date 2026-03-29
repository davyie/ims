package com.ims.application.port.inbound;

import com.ims.application.command.AdjustStorageStockCommand;
import com.ims.application.command.RegisterItemCommand;
import com.ims.application.command.UpdateItemCommand;
import com.ims.domain.model.Item;
import java.util.UUID;

public interface ItemCommandPort {
    Item registerItem(RegisterItemCommand command);
    Item updateItem(UpdateItemCommand command);
    Item adjustStorageStock(AdjustStorageStockCommand command);
    void deleteItem(UUID id);
}
