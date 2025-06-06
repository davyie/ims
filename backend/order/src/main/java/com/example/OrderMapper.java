package com.example;

public class OrderMapper {

    // Convert OrderEntity to OrderDTO
    public static OrderDTO toDTO(OrderEntity entity) {
        if (entity == null) {
            return null;
        }
        return new OrderDTO(
                entity.getName(),
                entity.getQuantity(),
                entity.getAmount(),
                entity.getStatus()
        );
    }

    // Convert OrderDTO to OrderEntity
    public static OrderEntity toEntity(OrderDTO dto) {
        if (dto == null) {
            return null;
        }
        OrderEntity entity = new OrderEntity();
        entity.setName(dto.getName());
        entity.setQuantity(dto.getQuantity());
        entity.setAmount(dto.getAmount());
        entity.setStatus(dto.getStatus());
        return entity;
    }
}

