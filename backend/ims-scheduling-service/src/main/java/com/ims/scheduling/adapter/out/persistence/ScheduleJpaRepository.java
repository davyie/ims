package com.ims.scheduling.adapter.out.persistence;

import com.ims.scheduling.domain.model.MarketSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ScheduleJpaRepository extends JpaRepository<MarketSchedule, UUID> {
}
