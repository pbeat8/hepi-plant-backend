package com.hepiplant.backend.service.impl;

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

    public static final String WATERING = "Podlewanie";
    public static final String WATERING_PLANT = "Podlewanie rośliny o nazwie ";
    public static final String MISTING = "Zraszanie";
    public static final String MISTING_PLANT = "Zraszanie rośliny o nazwie ";
    public static final String FERTILIZATING = "Nawożenie";
    public static final String FERRTILIZATING_PLANT = "Nawożenie rośliny o nazwie ";
    private final PlantRepository plantRepository;
    private final CategoryRepository categoryRepository;
    private final SpeciesRepository speciesRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final EventRepository eventRepository;
    private final BeanValidator beanValidator;
    

    public PlantServiceImpl(final PlantRepository plantRepository,
                            final CategoryRepository categoryRepository, final SpeciesRepository speciesRepository,
                            final UserRepository userRepository,
                            final ScheduleRepository scheduleRepository,
                            final EventRepository eventRepository,
                            final BeanValidator beanValidator) {
        this.plantRepository = plantRepository;
        this.categoryRepository = categoryRepository;
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
        schedule.setPlant(plant);
        if(plantDto.getSpecies()!=null && plantDto.getSpecies().getId()!=null){
            Species species = speciesRepository.findById(plantDto.getSpecies().getId()).orElseThrow(() -> new EntityNotFoundException("Species not found for id " + plantDto.getSpecies().getId()));
            Category category = categoryRepository.findById(plantDto.getCategoryId()).orElseThrow(() -> new EntityNotFoundException("Category not found for id " + plantDto.getCategoryId()));
            plant.setSpecies(species);
            if(!plantDto.getSpecies().getName().equals("Brak"))
                plant.setCategory(species.getCategory());
            else
                plant.setCategory(category);
            schedule.setWateringFrequency(plantDto.getSchedule().getWateringFrequency());
            schedule.setFertilizingFrequency(species.getFertilizingFrequency());
            schedule.setMistingFrequency(species.getMistingFrequency());
        }
        if(plantDto.getUserId()!=null){
            User user = userRepository.findById(plantDto.getUserId()).orElseThrow(() -> new EntityNotFoundException("User not found for id " + plantDto.getUserId()));
            plant.setUser(user);
        }
        Event eventW = new Event();
        Event eventF = new Event();
        Event eventM = new Event();
        List<Event> eventList = new ArrayList<>();
        if(plantDto.getSchedule()!=null){
            if(plantDto.getSchedule().getWateringFrequency()>0){
                eventW= addNewEvent(plant,  WATERING,WATERING_PLANT, plantDto.getSchedule().getWateringFrequency());
                eventList.add(eventW);
            }
            if(plantDto.getSchedule().getMistingFrequency()>0){
                eventM = addNewEvent(plant, MISTING,MISTING_PLANT, plantDto.getSchedule().getMistingFrequency());
                eventList.add(eventM);
            }
            if(plantDto.getSchedule().getFertilizingFrequency()>0){
                eventF = addNewEvent(plant, FERTILIZATING,FERRTILIZATING_PLANT, plantDto.getSchedule().getFertilizingFrequency());
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
            if(!plantDto.getSpecies().getName().equals("Brak"))
                plant.setCategory(species.getCategory());
        }
        if(plantDto.getUserId()!=null && !plantDto.getUserId().equals(plant.getUser().getId())){
            throw new ImmutableFieldException("Field User in Plant is immutable!");
        }
        if(plantDto.getPurchaseDate()!=null){
            plant.setPurchaseDate(plantDto.getPurchaseDate());
        }

        for (Event event: eventRepository.findAll()
                .stream().filter(e -> e.getPlant().getId().equals(plant.getId()))
                .filter(e -> !e.isDone())
                .collect(Collectors.toList())) {
            eventRepository.delete(event);
        }
        Event eventW = new Event();
        Event eventF = new Event();
        Event eventM = new Event();
        if(plantDto.getSchedule()!=null){
            if(plantDto.getSchedule().getWateringFrequency()>0){
                eventW= addNewEvent(plant,  WATERING,WATERING_PLANT, plantDto.getSchedule().getWateringFrequency());
            }
            if(plantDto.getSchedule().getMistingFrequency()>0){
                eventM = addNewEvent(plant, MISTING,MISTING_PLANT, plantDto.getSchedule().getMistingFrequency());
            }
            if(plantDto.getSchedule().getFertilizingFrequency()>0){
                eventF = addNewEvent(plant, FERTILIZATING,FERRTILIZATING_PLANT, plantDto.getSchedule().getFertilizingFrequency());
            }
        }

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

    private Event addNewEvent(Plant plant, String name, String longName, int days) {
        Event event = new Event();
        event.setDone(false);
        event.setEventDate(LocalDateTime.now().plusDays(days));
        event.setEventName(name);
        event.setEventDescription(longName+plant.getName());
        event.setPlant(plant);
        return event;
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
