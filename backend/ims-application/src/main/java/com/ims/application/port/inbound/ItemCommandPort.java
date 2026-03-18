package com.ims.application.port.inbound;

import com.ims.application.command.AdjustStorageStockCommand;
import com.ims.application.command.RegisterItemCommand;
import com.ims.application.command.UpdateItemCommand;
import com.ims.domain.model.Item;

public interface ItemCommandPort {
    Item registerItem(RegisterItemCommand command);
    Item updateItem(UpdateItemCommand command);
    Item adjustStorageStock(AdjustStorageStockCommand command);
}
