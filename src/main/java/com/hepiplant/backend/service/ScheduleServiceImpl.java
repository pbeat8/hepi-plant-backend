package com.hepiplant.backend.service;

import com.hepiplant.backend.entity.Schedule;
import com.hepiplant.backend.repository.ScheduleRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;

public class ScheduleServiceImpl implements ScheduleService{
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
