package com.hepiplant.backend.service;

import com.hepiplant.backend.entity.Schedule;

import java.util.List;

public interface ScheduleService {
    List<Schedule> getAll();
    Schedule getById(Long id);
}
