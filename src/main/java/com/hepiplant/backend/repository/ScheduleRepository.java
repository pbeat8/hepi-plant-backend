package com.hepiplant.backend.repository;

import com.hepiplant.backend.dto.ScheduleDto;
import com.hepiplant.backend.entity.Plant;
import com.hepiplant.backend.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllByPlant(Plant plant);
}
