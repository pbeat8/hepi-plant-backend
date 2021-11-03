package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.EventDto;
import com.hepiplant.backend.dto.PlantDto;
import com.hepiplant.backend.entity.*;
import com.hepiplant.backend.exception.ImmutableFieldException;
import com.hepiplant.backend.mapper.DtoMapper;
import com.hepiplant.backend.repository.*;
import com.hepiplant.backend.service.PlantService;
import com.hepiplant.backend.validator.BeanValidator;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final EventRepository eventRepository;
    private final BeanValidator beanValidator;

    public PlantServiceImpl(final PlantRepository plantRepository,
                            final SpeciesRepository speciesRepository,
                            final UserRepository userRepository,
                            final ScheduleRepository scheduleRepository,
                            EventRepository eventRepository, final BeanValidator beanValidator) {
        this.plantRepository = plantRepository;
        this.speciesRepository = speciesRepository;
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
        this.eventRepository = eventRepository;
        this.beanValidator = beanValidator;
    }

    @Transactional
    public PlantDto create(PlantDto plantDto){
        Plant plant = new Plant();
        plant.setName(plantDto.getName());
        plant.setPurchaseDate(plantDto.getPurchaseDate());
        plant.setLocation(plantDto.getLocation());
        plant.setPhoto(plantDto.getPhoto());
        Schedule schedule = new Schedule();
        Event eventW = new Event();
        Event eventF = new Event();
        Event eventM = new Event();
        List<Event> eventList = new ArrayList<Event>();
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
            if(schedule.getWateringFrequency()>0){
                eventW.setDone(false);
                eventW.setEventDate(LocalDateTime.now().plusDays(schedule.getWateringFrequency()));
                eventW.setEventName("Podlewanie");
                eventW.setEventDescription("Podlewanie rośliny o nazwie"+plant.getName());
                eventW.setPlant(plant);
                eventList.add(eventW);
            }
            if(schedule.getMistingFrequency()>0){
                eventM.setDone(false);
                eventM.setEventDate(LocalDateTime.now().plusDays(schedule.getMistingFrequency()));
                eventM.setEventName("Zraszanie");
                eventM.setEventDescription("Zraszanie rośliny o nazwie"+plant.getName());
                eventM.setPlant(plant);
                eventList.add(eventM);
            }
            if(schedule.getFertilizingFrequency()>0){
                eventF.setDone(false);
                eventF.setEventDate(LocalDateTime.now().plusDays(schedule.getFertilizingFrequency()));
                eventF.setEventName("Nawożenie");
                eventF.setEventDescription("Nawożenie rośliny o nazwie"+plant.getName());
                eventF.setPlant(plant);
                eventList.add(eventF);
            }
        }

        plant.setSchedule(schedule);

        if(eventList.size()>0)
            plant.setEventList(eventList);
        beanValidator.validate(plant);
        Plant savedPlant = plantRepository.save(plant);
        scheduleRepository.save(schedule);
        if(schedule.getWateringFrequency()>0)
            eventRepository.save(eventW);
        if(schedule.getMistingFrequency()>0)
            eventRepository.save(eventM);
        if(schedule.getFertilizingFrequency()>0)
            eventRepository.save(eventF);
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
        if(plantDto.getPhoto()!=null && !plantDto.getPhoto().isEmpty()){
            plant.setPhoto(plantDto.getPhoto());
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
        if(plantDto.getPurchaseDate()!=null){
            plant.setPurchaseDate(plantDto.getPurchaseDate());
        }
        Event eventW = new Event();
        Event eventF = new Event();
        Event eventM = new Event();
        List<Event> eventList = new ArrayList<Event>();
        for (Event event: eventRepository.findAll()
                .stream().filter(e -> e.getPlant().getId().equals(plant.getId()))
                .filter(e -> !e.isDone())
                .collect(Collectors.toList())) {
            eventRepository.delete(event);
        }

        if(plantDto.getSchedule()!=null){
            if(plantDto.getSchedule().getWateringFrequency()>0){
                eventW.setDone(false);
                eventW.setEventDate(LocalDateTime.now().plusDays(plantDto.getSchedule().getWateringFrequency()));
                eventW.setEventName("Podlewanie");
                eventW.setEventDescription("Podlewanie rośliny o nazwie"+plant.getName());
                eventW.setPlant(plant);
                eventList.add(eventW);
            }
            if(plantDto.getSchedule().getMistingFrequency()>0){
                eventM.setDone(false);
                eventM.setEventDate(LocalDateTime.now().plusDays(plantDto.getSchedule().getMistingFrequency()));
                eventM.setEventName("Zraszanie");
                eventM.setEventDescription("Zraszanie rośliny o nazwie"+plant.getName());
                eventM.setPlant(plant);
                eventList.add(eventM);
            }
            if(plantDto.getSchedule().getFertilizingFrequency()>0){
                eventF.setDone(false);
                eventF.setEventDate(LocalDateTime.now().plusDays(plantDto.getSchedule().getFertilizingFrequency()));
                eventF.setEventName("Nawożenie");
                eventF.setEventDescription("Nawożenie rośliny o nazwie"+plant.getName());
                eventF.setPlant(plant);
                eventList.add(eventF);
            }
        }

        if(eventList.size()>0)
            plant.setEventList(eventList);
        beanValidator.validate(plant);
        Plant savedPlant = plantRepository.save(plant);
        if(plantDto.getSchedule().getWateringFrequency()>0)
            eventRepository.save(eventW);
        if(plantDto.getSchedule().getMistingFrequency()>0)
            eventRepository.save(eventM);
        if(plantDto.getSchedule().getFertilizingFrequency()>0)
            eventRepository.save(eventF);
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
