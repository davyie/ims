package com.ims.warehouse.adapter.out.persistence;

import com.ims.warehouse.domain.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ItemJpaRepository extends JpaRepository<Item, UUID> {

    Page<Item> findByUserId(UUID userId, Pageable pageable);

    boolean existsByUserIdAndSku(UUID userId, String sku);
}
