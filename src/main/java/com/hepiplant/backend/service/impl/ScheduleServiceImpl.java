package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.entity.Schedule;
import com.hepiplant.backend.repository.ScheduleRepository;
import com.hepiplant.backend.service.ScheduleService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    public ScheduleRepository scheduleRepository;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public List<Schedule> getAll() {
        return scheduleRepository.findAll();
    }

    @Override
    public Schedule getById(Long id) {
        return scheduleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
    }
}
