package com.ims.application.port.inbound;

import com.ims.application.query.GetItemQuery;
import com.ims.application.query.ListItemsQuery;
import com.ims.domain.model.Item;

import java.util.List;

public interface ItemQueryPort {
    Item getItem(GetItemQuery query);
    List<Item> listItems(ListItemsQuery query);
}
