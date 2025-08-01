package com.example.warehouse.commands;

import com.example.warehouse.domain.Warehouse;
import com.example.warehouse.domain.WarehouseItem;
import com.example.warehouse.domain.WarehouseItemQuantity;
import com.example.warehouse.repository.WarehouseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Iterator;
import java.util.List;

public class DecrementWarehouseItemCommand {

    private WarehouseRepository warehouseRepository;
    private Logger logger = LoggerFactory.getLogger(DecrementWarehouseItemCommand.class);
    @Autowired
    public DecrementWarehouseItemCommand(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    public ResponseEntity execute(String warehouseId) {
        if (warehouseRepository.findById(warehouseId) == null) {return new ResponseEntity<>(HttpStatus.NOT_FOUND);}
        Warehouse warehouse = warehouseRepository.findById(warehouseId).orElseThrow();
        List<WarehouseItemQuantity> list = warehouse.getInventory();
        Iterator it = list.iterator();
        while(it.hasNext()) {
            WarehouseItemQuantity iq = (WarehouseItemQuantity) it.next();
            WarehouseItem item =  iq.getItem();
            if (item.getItemId() == iq.getItem().getItemId()) {
                iq.decrementQuantity(iq.getQuantity());
            }
        }
        logger.info("From WarehouseController...");
        for (WarehouseItemQuantity wiq : list) {
            WarehouseItem i = wiq.getItem();
            logger.info("Item {}", i.getName());
            logger.info("Quantity: {}", wiq.getQuantity());
        }

        warehouse.setInventory(list);
        warehouseRepository.save(warehouse);
        return new ResponseEntity(HttpStatus.OK);
    }
}
