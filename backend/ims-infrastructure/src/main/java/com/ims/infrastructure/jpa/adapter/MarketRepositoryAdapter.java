package com.ims.infrastructure.jpa.adapter;

import com.ims.domain.model.Market;
import com.ims.domain.model.MarketStatus;
import com.ims.domain.port.MarketRepositoryPort;
import com.ims.infrastructure.jpa.entity.MarketJpaEntity;
import com.ims.infrastructure.jpa.repository.MarketJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class MarketRepositoryAdapter implements MarketRepositoryPort {

    private final MarketJpaRepository jpaRepository;

    public MarketRepositoryAdapter(MarketJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Market save(Market market) {
        return toDomain(jpaRepository.save(toEntity(market)));
    }

    @Override
    public Optional<Market> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Market> findAll(MarketStatus status) {
        if (status != null) {
            return jpaRepository.findByStatus(MarketJpaEntity.MarketStatusJpa.valueOf(status.name()))
                    .stream().map(this::toDomain).collect(Collectors.toList());
        }
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    private MarketJpaEntity toEntity(Market m) {
        MarketJpaEntity e = new MarketJpaEntity();
        e.setId(m.getId());
        e.setName(m.getName());
        e.setPlace(m.getPlace());
        e.setOpenDate(m.getOpenDate());
        e.setCloseDate(m.getCloseDate());
        e.setStatus(MarketJpaEntity.MarketStatusJpa.valueOf(m.getStatus().name()));
        e.setCreatedAt(m.getCreatedAt());
        return e;
    }

    private Market toDomain(MarketJpaEntity e) {
        return new Market(e.getId(), e.getName(), e.getPlace(), e.getOpenDate(), e.getCloseDate(),
            MarketStatus.valueOf(e.getStatus().name()), e.getCreatedAt());
    }
}
