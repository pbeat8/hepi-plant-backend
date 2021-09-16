package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.PlantDto;
import com.hepiplant.backend.dto.ScheduleDto;
import com.hepiplant.backend.entity.Plant;
import com.hepiplant.backend.entity.Schedule;
import com.hepiplant.backend.entity.Species;
import com.hepiplant.backend.entity.User;
import com.hepiplant.backend.exception.ImmutableFieldException;
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
        if(plantDto.getSpeciesId()!=null){
            Species species = speciesRepository.findById(plantDto.getSpeciesId()).orElseThrow(() -> new EntityNotFoundException("Species not found for id " + plantDto.getSpeciesId()));
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
        scheduleRepository.save(schedule);
        Plant savedPlant = plantRepository.save(plant);
        return mapToDto(savedPlant);
    }

    @Override
    public List<PlantDto> getAll() {
        return plantRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlantDto> getAllByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found for id " + userId));
        return user.getPlantList().stream()
                .map(this::mapToDto)
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
        if(plantDto.getSpeciesId()!=null && (plant.getSpecies()==null || (plant.getSpecies().getId()!=null && !plantDto.getSpeciesId().equals(plant.getSpecies().getId())))){
            Species species = speciesRepository.findById(plantDto.getSpeciesId()).orElseThrow(EntityNotFoundException::new);
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

    private PlantDto mapToDto(Plant plant){
        PlantDto dto = new PlantDto();
        dto.setId(plant.getId());
        dto.setName(plant.getName());
        dto.setPurchaseDate(plant.getPurchaseDate());
        dto.setLocation(plant.getLocation());
        if(plant.getCategory()!=null) {
            dto.setCategoryId(plant.getCategory().getId());
        }
        if(plant.getSpecies()!=null) {
            dto.setSpeciesId(plant.getSpecies().getId());
        }
        if(plant.getUser()!=null){
            dto.setUserId(plant.getUser().getId());
        }
        if(plant.getSchedule() != null){
            dto.setSchedule(mapToDto(plant.getSchedule()));
        }
        return dto;
    }

    private ScheduleDto mapToDto(Schedule schedule){
        ScheduleDto dto = new ScheduleDto();
        dto.setWateringFrequency(schedule.getWateringFrequency());
        dto.setFertilizingFrequency(schedule.getFertilizingFrequency());
        dto.setMistingFrequency(schedule.getMistingFrequency());
        return dto;
    }
}
