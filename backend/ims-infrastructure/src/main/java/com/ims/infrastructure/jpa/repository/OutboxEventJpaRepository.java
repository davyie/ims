package com.ims.infrastructure.jpa.repository;

import com.ims.infrastructure.jpa.entity.OutboxEventJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventJpaEntity, UUID> {
    List<OutboxEventJpaEntity> findByStatus(String status);
}
