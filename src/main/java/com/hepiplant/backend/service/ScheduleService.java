package com.hepiplant.backend.service;

import com.hepiplant.backend.dto.ScheduleDto;
import com.hepiplant.backend.entity.Schedule;

import java.util.List;

public interface ScheduleService {
    List<ScheduleDto> getAll();
    ScheduleDto getById(Long id);
    ScheduleDto add(ScheduleDto scheduleDto);
    ScheduleDto update(Long id, ScheduleDto scheduleDto);
    String delete(Long id);
}
