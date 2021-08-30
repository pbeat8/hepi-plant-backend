package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.ScheduleDto;
import com.hepiplant.backend.entity.Schedule;
import com.hepiplant.backend.repository.ScheduleRepository;
import com.hepiplant.backend.service.ScheduleService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    public ScheduleRepository scheduleRepository;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public List<ScheduleDto> getAll() {
        return scheduleRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public ScheduleDto getById(Long id) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return mapToDto(schedule);
    }

    @Override
    public ScheduleDto add(ScheduleDto scheduleDto) {
        Schedule schedule = new Schedule();
        if(scheduleDto.getWateringFrequency()>=0)
        schedule.setWateringFrequency(scheduleDto.getWateringFrequency());
        if(scheduleDto.getFertilizingFrequency()>=0)
        schedule.setFertilizingFrequency(scheduleDto.getFertilizingFrequency());
        if(scheduleDto.getMistingFrequency()>=0)
        schedule.setMistingFrequency(scheduleDto.getMistingFrequency());
        Schedule savedSchedule = scheduleRepository.save(schedule);
        return mapToDto(savedSchedule);
    }

    @Override
    public ScheduleDto update(Long id, ScheduleDto scheduleDto) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if(scheduleDto.getWateringFrequency()!=schedule.getWateringFrequency() && scheduleDto.getWateringFrequency()>=0)
        schedule.setWateringFrequency(scheduleDto.getWateringFrequency());
        if(scheduleDto.getFertilizingFrequency()!=schedule.getFertilizingFrequency() && scheduleDto.getFertilizingFrequency()>=0)
        schedule.setFertilizingFrequency(scheduleDto.getFertilizingFrequency());
        if(scheduleDto.getMistingFrequency()!=schedule.getMistingFrequency() && scheduleDto.getMistingFrequency()>=0)
        schedule.setMistingFrequency(scheduleDto.getMistingFrequency());
        Schedule savedSchedule = scheduleRepository.save(schedule);
        return mapToDto(savedSchedule);
    }

    @Override
    public String delete(Long id) {

        Optional<Schedule> schedule = scheduleRepository.findById(id);
        if(schedule.isEmpty()){
            return "No schedule with id = "+id;
        }
        scheduleRepository.delete(schedule.get());
        return "Successfully deleted the schedule with id = "+ id;
    }
    private ScheduleDto mapToDto(Schedule schedule){
        ScheduleDto dto = new ScheduleDto();
        dto.setWateringFrequency(schedule.getWateringFrequency());
        dto.setFertilizingFrequency(schedule.getFertilizingFrequency());
        dto.setMistingFrequency(schedule.getMistingFrequency());
        return dto;
    }
}
