package com.hepiplant.backend.service;

import com.hepiplant.backend.dto.ScheduleDto;

import java.util.List;

public interface ScheduleService {
    List<ScheduleDto> getAll();
    ScheduleDto getById(Long id);
    List<ScheduleDto> getAllByPlant(Long plantId);
    ScheduleDto add(ScheduleDto scheduleDto);
    ScheduleDto update(Long id, ScheduleDto scheduleDto);
    String delete(Long id);
}
