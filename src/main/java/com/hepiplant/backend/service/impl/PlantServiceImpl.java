package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.PlantDto;
import com.hepiplant.backend.entity.Plant;
import com.hepiplant.backend.entity.Schedule;
import com.hepiplant.backend.entity.Species;
import com.hepiplant.backend.entity.User;
import com.hepiplant.backend.exception.ImmutableFieldException;
import com.hepiplant.backend.mapper.DtoMapper;
import com.hepiplant.backend.repository.PlantRepository;
import com.hepiplant.backend.repository.ScheduleRepository;
import com.hepiplant.backend.repository.SpeciesRepository;
import com.hepiplant.backend.repository.UserRepository;
import com.hepiplant.backend.service.PlantService;
import com.hepiplant.backend.validator.BeanValidator;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hepiplant.backend.mapper.DtoMapper.mapToDto;

@Service
public class PlantServiceImpl implements PlantService {

    private final PlantRepository plantRepository;
    private final SpeciesRepository speciesRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final BeanValidator beanValidator;

    public PlantServiceImpl(PlantRepository plantRepository, SpeciesRepository speciesRepository,
                            UserRepository userRepository, ScheduleRepository scheduleRepository,
                            BeanValidator beanValidator) {
        this.plantRepository = plantRepository;
        this.speciesRepository = speciesRepository;
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
        this.beanValidator = beanValidator;
    }

    @Transactional
    public PlantDto create(PlantDto plantDto){
        Plant plant = new Plant();
        plant.setName(plantDto.getName());
        plant.setPurchaseDate(plantDto.getPurchaseDate());
        plant.setLocation(plantDto.getLocation());
        Schedule schedule = new Schedule();
        schedule.setPlant(plant);
        if(plantDto.getSpecies()!=null && plantDto.getSpecies().getId()!=null){
            Species species = speciesRepository.findById(plantDto.getSpecies().getId()).orElseThrow(() -> new EntityNotFoundException("Species not found for id " + plantDto.getSpecies().getId()));
            plant.setSpecies(species);
            plant.setCategory(species.getCategory());
            schedule.setWateringFrequency(species.getWateringFrequency());
            schedule.setFertilizingFrequency(species.getFertilizingFrequency());
            schedule.setMistingFrequency(species.getMistingFrequency());
        }
        if(plantDto.getUserId()!=null){
            User user = userRepository.findById(plantDto.getUserId()).orElseThrow(() -> new EntityNotFoundException("User not found for id " + plantDto.getUserId()));
            plant.setUser(user);
        }
        if(plantDto.getSchedule()!=null){
            schedule.setWateringFrequency(plantDto.getSchedule().getWateringFrequency());
            schedule.setFertilizingFrequency(plantDto.getSchedule().getFertilizingFrequency());
            schedule.setMistingFrequency(plantDto.getSchedule().getMistingFrequency());
        }
        plant.setSchedule(schedule);
        beanValidator.validate(plant);
        Plant savedPlant = plantRepository.save(plant);
        scheduleRepository.save(schedule);
        return mapToDto(savedPlant);
    }

    @Override
    public List<PlantDto> getAll() {
        return plantRepository.findAll().stream()
                .map(DtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlantDto> getAllByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found for id " + userId));
        return user.getPlantList().stream()
                .map(DtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PlantDto getById(Long id) {
        Plant plant = plantRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Plant not found for id " + id));
        return mapToDto(plant);
    }

    @Override
    public PlantDto update(Long id, PlantDto plantDto) {
        Plant plant = plantRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Plant not found for id " + id));
        if(plantDto.getName()!=null && !plantDto.getName().isEmpty()){
            plant.setName(plantDto.getName());
        }
        if(plantDto.getLocation()!=null && !plantDto.getLocation().isEmpty()){
            plant.setLocation(plantDto.getLocation());
        }
        if(plantDto.getCategoryId()!=null && !plantDto.getCategoryId().equals(plant.getCategory().getId())){
            throw new ImmutableFieldException("Field Category in Plant is immutable!");
        }
        if(plantDto.getSpecies()!=null && plantDto.getSpecies().getId()!=null){
            Species species = speciesRepository.findById(plantDto.getSpecies().getId()).orElseThrow(EntityNotFoundException::new);
            plant.setSpecies(species);
            plant.setCategory(species.getCategory());
        }
        if(plantDto.getUserId()!=null && !plantDto.getUserId().equals(plant.getUser().getId())){
            throw new ImmutableFieldException("Field User in Plant is immutable!");
        }
        beanValidator.validate(plant);
        Plant savedPlant = plantRepository.save(plant);
        return mapToDto(savedPlant);
    }

    @Override
    public String delete(Long id) {
        Optional<Plant> plant = plantRepository.findById(id);
        if(plant.isEmpty()){
            return "No plant with id = " + id;
        }
        plantRepository.delete(plant.get());
        return "Successfully deleted the plant with id = "+ id;
    }
}
