package com.example.common.clients;

import com.example.warehouse.domain.DecrementQuantity;
import com.example.warehouse.domain.WarehouseItem;
import com.example.warehouse.domain.WarehouseItemQuantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;


@Component
public class WarehouseClient {
    private RestTemplate restTemplate;
    private String url = "http://localhost:8080/warehouse";

    @Autowired
    public  WarehouseClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getItemByItemId(String warebouseId, Integer itemId) {
        String warehousePath = "warehouseId=" + warebouseId;
        String itemItPath = "itemId=" + itemId;
        return restTemplate.getForObject(url + "/inventory/get?" + warehousePath + "&" + itemItPath , String.class);
    }

    public void decrementWarehouseItem(String warehouseId, Integer itemId, Integer quantity) {
        DecrementQuantity dq = new DecrementQuantity();
        dq.setItemId(itemId);
        dq.setQuantity(quantity);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(dq, httpHeaders);
        restTemplate.put(url + "/inventory/decrement?warehouseId=" + warehouseId, entity, String.class);
    }
}
