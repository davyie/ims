package com.example.warehouse.commands;

import com.example.warehouse.domain.Warehouse;
import com.example.warehouse.domain.WarehouseItem;
import com.example.warehouse.domain.WarehouseItemQuantity;
import com.example.warehouse.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Iterator;
import java.util.List;

public class IncrementWarehouseItemCommand {

    private WarehouseRepository warehouseRepository;

    @Autowired
    public IncrementWarehouseItemCommand(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    public ResponseEntity execute(String warehouseId) {
        if (warehouseRepository.findById(warehouseId).isEmpty()) {return new ResponseEntity<>(HttpStatus.NOT_FOUND);}
        Warehouse warehouse = warehouseRepository.findById(warehouseId).orElseThrow();
        List<WarehouseItemQuantity> list = warehouse.getInventory();
        Iterator it = list.iterator();
        while(it.hasNext()) {
            WarehouseItemQuantity iq = (WarehouseItemQuantity) it.next();
            WarehouseItem item = iq.getItem();
            if (item.getItemId() == iq.getItem().getItemId()) {
                iq.incrementQuantity(iq.getQuantity());
            }
        }
        warehouse.setInventory(list);
        warehouseRepository.save(warehouse);
        return new ResponseEntity(HttpStatus.OK);
    }
}
