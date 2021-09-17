package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.ScheduleDto;
import com.hepiplant.backend.entity.Plant;
import com.hepiplant.backend.entity.Schedule;
import com.hepiplant.backend.exception.ImmutableFieldException;
import com.hepiplant.backend.repository.PlantRepository;
import com.hepiplant.backend.repository.ScheduleRepository;
import com.hepiplant.backend.service.ScheduleService;
import com.hepiplant.backend.validator.BeanValidator;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final PlantRepository plantRepository;
    private final BeanValidator beanValidator;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository, PlantRepository plantRepository, BeanValidator beanValidator) {
        this.scheduleRepository = scheduleRepository;
        this.plantRepository = plantRepository;
        this.beanValidator = beanValidator;
    }

    @Override
    public List<ScheduleDto> getAll() {
        return scheduleRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public ScheduleDto getById(Long id) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Schedule not found for id "+id));
        return mapToDto(schedule);
    }

    @Override
    public List<ScheduleDto> getAllByPlant(Long plantId) {
        Plant plant = plantRepository.findById(plantId).orElseThrow(() -> new EntityNotFoundException("Plant not found for id " + plantId));
        return scheduleRepository.findAllByPlant(plant).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public ScheduleDto add(ScheduleDto scheduleDto) {
        Schedule schedule = new Schedule();
        if(scheduleDto.getPlantId()!=null){
            Plant plant = plantRepository.findById(scheduleDto.getPlantId()).orElseThrow(() ->new EntityNotFoundException("Plant not found for id " + scheduleDto.getPlantId()));
            schedule.setPlant(plant);
        }

        schedule.setWateringFrequency(scheduleDto.getWateringFrequency());
        schedule.setFertilizingFrequency(scheduleDto.getFertilizingFrequency());
        schedule.setMistingFrequency(scheduleDto.getMistingFrequency());

        beanValidator.validate(schedule);
        Schedule savedSchedule = scheduleRepository.save(schedule);
        return mapToDto(savedSchedule);
    }

    @Override
    public ScheduleDto update(Long id, ScheduleDto scheduleDto) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Schedule not found for id "+id));
        if(scheduleDto.getPlantId()!=null){
            throw new ImmutableFieldException("Field Plant in Schedule is immutable!");
        }
        schedule.setWateringFrequency(scheduleDto.getWateringFrequency());
        schedule.setFertilizingFrequency(scheduleDto.getFertilizingFrequency());
        schedule.setMistingFrequency(scheduleDto.getMistingFrequency());
        beanValidator.validate(schedule);
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
        dto.setId(schedule.getId());
        if(schedule.getPlant()!=null){
            dto.setPlantId(schedule.getPlant().getId());
        }
        dto.setWateringFrequency(schedule.getWateringFrequency());
        dto.setFertilizingFrequency(schedule.getFertilizingFrequency());
        dto.setMistingFrequency(schedule.getMistingFrequency());
        return dto;
    }
}
