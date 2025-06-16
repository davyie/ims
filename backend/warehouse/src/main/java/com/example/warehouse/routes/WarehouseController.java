package com.example.warehouse.routes;

import com.example.common.domain.Item;
import com.example.common.domain.ItemQuantity;
import com.example.warehouse.domain.WarehouseItemQuantity;
import com.example.warehouse.repository.WarehouseRepository;
import com.example.warehouse.domain.Warehouse;
import com.example.warehouse.domain.WarehouseItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/warehouse")
public class WarehouseController {

    private final Logger logger = LoggerFactory.getLogger(WarehouseController.class);
    private WarehouseRepository warehouseRepository;

    @Autowired
    public WarehouseController(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @GetMapping("/")
    public ResponseEntity<String> HelloWorld() {
        return new ResponseEntity<>("Hello from Warehouse Controller!", HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/get/all")
    public ResponseEntity<List<Warehouse>> getAllWarehouses() {
        return new ResponseEntity<>(warehouseRepository.findAll(), HttpStatus.ACCEPTED);
    }

    /**
     * Endpoint to add Warehouse
     * @return
     */
    @PostMapping("/add")
    public ResponseEntity<Warehouse> AddWarehouse() {
        return new ResponseEntity<>(warehouseRepository.save(new Warehouse()), HttpStatus.OK);
    }

    /**
     * Endpoint is used to add item to Warehouse
     * @param warehouseId
     * @param itemQuantity
     * @return
     */
    @PutMapping("/inventory/add")
    public ResponseEntity<WarehouseItemQuantity> AddItemToWarehouse(@RequestParam String warehouseId, @RequestBody WarehouseItemQuantity itemQuantity) {
        if (!warehouseRepository.existsById(warehouseId)) {return new ResponseEntity<>(HttpStatus.NOT_FOUND);}
        Warehouse warehouse = warehouseRepository.findById(warehouseId).orElseThrow();
        List<WarehouseItemQuantity> list = warehouse.getInventory() == null ? new ArrayList<>() : warehouse.getInventory();
        list.add(itemQuantity);
        warehouse.setInventory(list);
        warehouseRepository.save(warehouse);
        return new ResponseEntity<>(itemQuantity, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/inventory/delete")
    public ResponseEntity<Warehouse> DeleteItemFromWarehose(@RequestParam String warehouseId, @RequestBody Integer itemId) {
        if(warehouseRepository.findById(warehouseId) == null) {return new ResponseEntity<>(HttpStatus.NOT_FOUND);}
        Warehouse warehouse = warehouseRepository.findById(warehouseId).orElseThrow();
        List<WarehouseItemQuantity> warehouseItems = warehouse.getInventory().stream().filter(iq -> {
            WarehouseItem item = (WarehouseItem) iq.getItem();
            return item.getItemId() != itemId;
        }).toList();
        warehouse.setInventory(warehouseItems);
        warehouseRepository.save(warehouse);
        return new ResponseEntity<>(warehouse, HttpStatus.OK);
    }
//
    @PutMapping("/inventory/decrement")
    public ResponseEntity<Warehouse> DecrementItemFromWarehouse(@RequestParam String warehouseId, @RequestBody WarehouseItemQuantity warehouseItemQuantity) {
        if (warehouseRepository.findById(warehouseId) == null) {return new ResponseEntity<>(HttpStatus.NOT_FOUND);}
        Warehouse warehouse = warehouseRepository.findById(warehouseId).orElseThrow();
        List<WarehouseItemQuantity> list = warehouse.getInventory();
        Iterator it = list.iterator();
        WarehouseItem paramItem = warehouseItemQuantity.getItem();
        while(it.hasNext()) {
            WarehouseItemQuantity iq = (WarehouseItemQuantity) it.next();
            WarehouseItem item =  iq.getItem();
            if (item.getItemId() == paramItem.getItemId()) {
                iq.decrementQuantity(warehouseItemQuantity.getQuantity());
            }
        }
        warehouse.setInventory(list);
        warehouseRepository.save(warehouse);
        return new ResponseEntity<>(warehouse, HttpStatus.OK);
    }
//
    @PutMapping("/inventory/increment")
    public ResponseEntity<Warehouse> IncrementIteminWarehouse(@RequestParam String warehouseId, @RequestBody WarehouseItemQuantity warehouseItemQuantity) {
        if (warehouseRepository.findById(warehouseId) == null) {return new ResponseEntity<>(HttpStatus.NOT_FOUND);}
        Warehouse warehouse = warehouseRepository.findById(warehouseId).orElseThrow();
        List<WarehouseItemQuantity> list = warehouse.getInventory();
        Iterator it = list.iterator();
        WarehouseItem paramItem = warehouseItemQuantity.getItem();
        while(it.hasNext()) {
            WarehouseItemQuantity iq = (WarehouseItemQuantity) it.next();
            WarehouseItem item = iq.getItem();
            if (item.getItemId() == paramItem.getItemId()) {
                iq.incrementQuantity(warehouseItemQuantity.getQuantity());
            }
        }
        warehouse.setInventory(list);
        warehouseRepository.save(warehouse);
        return new ResponseEntity<>(warehouse, HttpStatus.OK);
    }
}
