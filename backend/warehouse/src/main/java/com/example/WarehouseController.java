package com.example;

import com.example.dtos.WarehouseItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/warehouse")
public class WarehouseController {

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
    public ResponseEntity<List<WarehouseItemDTO>> getAllWarehouseItems() {
        List<WarehouseItemDTO> list = warehouseRepository.findAll().stream().map(i -> new WarehouseItemDTO(i.getName(), i.getDescription(), i.getItemId())).toList();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<WarehouseItemDTO> addWarehouseItem(@RequestBody WarehouseItemDTO dto) {
        if (warehouseRepository.findByName(dto.getName()).isPresent()) {return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);}
        WarehouseItem item = new WarehouseItem(dto.getName(), dto.getDescription());
        warehouseRepository.insert(item);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<WarehouseItemDTO> deleteItem (@RequestParam String name) {
        try {
            WarehouseItem item = warehouseRepository.deleteByName(name).orElseThrow(() -> new RuntimeException("Item not found"));
            return new ResponseEntity<>(new WarehouseItemDTO(item.getName(), item.getDescription()), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/update")
    public ResponseEntity<WarehouseItemDTO> updateWarehouseItem(@RequestBody WarehouseItemDTO dto, @RequestParam String name) {
        try {
            WarehouseItem item = warehouseRepository.findByName(name).orElseThrow(() -> new RuntimeException("Item not found"));
            item.setName(dto.getName());
            item.setDescription(dto.getDescription());
            warehouseRepository.save(item);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
}
