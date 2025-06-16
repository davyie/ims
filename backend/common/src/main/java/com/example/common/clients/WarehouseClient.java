package com.example.common.clients;

import com.example.warehouse.domain.ChangeQuantity;
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
        ChangeQuantity cq = new ChangeQuantity();
        cq.setItemId(itemId);
        cq.setQuantity(quantity);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(cq, httpHeaders);
        restTemplate.put(url + "/inventory/decrement?warehouseId=" + warehouseId, entity, String.class);
    }

    public void incrementWarehouseItem(String warehouseId, Integer itemId, Integer quantity) {
        ChangeQuantity cq = new ChangeQuantity();
        cq.setItemId(itemId);
        cq.setQuantity(quantity);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(cq, headers);
        restTemplate.put(url + "/inventory/increment?warehouseId=" + warehouseId, entity, String.class);
    }
}
