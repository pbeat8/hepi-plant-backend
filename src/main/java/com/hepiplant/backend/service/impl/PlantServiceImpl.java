package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.PlantDto;
import com.hepiplant.backend.dto.PostDto;
import com.hepiplant.backend.dto.SpeciesDto;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.hepiplant.backend.mapper.DtoMapper.mapToDto;
import static java.util.Optional.ofNullable;

@Service
public class PlantServiceImpl implements PlantService {

    public static final String WATERING = "Podlewanie";
    public static final String WATERING_PLANT = "Podlewanie rośliny o nazwie ";
    public static final String MISTING = "Zraszanie";
    public static final String MISTING_PLANT = "Zraszanie rośliny o nazwie ";
    public static final String FERTILIZING = "Nawożenie";
    public static final String FERTILIZING_PLANT = "Nawożenie rośliny o nazwie ";
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
        ofNullable(plantDto.getSpecies()).ifPresent(s -> {
            Species species = speciesRepository.findById(plantDto.getSpecies().getId()).orElseThrow(() -> new EntityNotFoundException("Species not found for id " + plantDto.getSpecies().getId()));
            plant.setSpecies(species);
            if(!plantDto.getSpecies().getName().equals("Brak")) {
                plant.setCategory(species.getCategory());
            }
            else if(plantDto.getCategoryId()!=null && plantDto.getSpecies().getName().equals("Brak")){
                Category category = categoryRepository.findById(plantDto.getCategoryId()).orElseThrow(() -> new EntityNotFoundException("Category not found for id " + plantDto.getCategoryId()));
                plant.setCategory(category);
            }
            else{
                Category category = categoryRepository.findByName("Brak").orElseThrow(() -> new EntityNotFoundException("Category not found for name Brak"));
                plant.setCategory(category);
            }
            schedule.setWateringFrequency(plantDto.getSchedule().getWateringFrequency());
            schedule.setFertilizingFrequency(species.getFertilizingFrequency());
            schedule.setMistingFrequency(species.getMistingFrequency());

        });

        final String[] userHour = {null};
        ofNullable(plantDto.getUserId()).ifPresent(u -> {
            User user = userRepository.findById(plantDto.getUserId()).orElseThrow(() -> new EntityNotFoundException("User not found for id " + plantDto.getUserId()));
            userHour[0] = user.getHourOfNotifications();
            plant.setUser(user);
        });

        final Event[] eventW = {new Event()};
        final Event[] eventF = {new Event()};
        final Event[] eventM = {new Event()};
        List<Event> eventList = new ArrayList<>();
        ofNullable(plantDto.getSchedule()).ifPresent(s -> {
            if(plantDto.getSchedule().getWateringFrequency()>0){
                eventW[0] = addNewEvent(plant,  WATERING,WATERING_PLANT, plantDto.getSchedule().getWateringFrequency(), userHour[0]);
                eventList.add(eventW[0]);
            }
            if(plantDto.getSchedule().getMistingFrequency()>0){
                eventM[0] = addNewEvent(plant, MISTING,MISTING_PLANT, plantDto.getSchedule().getMistingFrequency(), userHour[0]);
                eventList.add(eventM[0]);
            }
            if(plantDto.getSchedule().getFertilizingFrequency()>0){
                eventF[0] = addNewEvent(plant, FERTILIZING, FERTILIZING_PLANT, plantDto.getSchedule().getFertilizingFrequency(), userHour[0]);
                eventList.add(eventF[0]);
            }
        });

        plant.setSchedule(schedule);
        if(eventList.size()>0)
            plant.setEventList(eventList);
        beanValidator.validate(plant);
        Plant savedPlant = plantRepository.save(plant);
        scheduleRepository.save(schedule);
        if(schedule.getWateringFrequency()>0)
            eventRepository.save(eventW[0]);
        if(schedule.getMistingFrequency()>0)
            eventRepository.save(eventM[0]);
        if(schedule.getFertilizingFrequency()>0)
            eventRepository.save(eventF[0]);
        return mapToDto(savedPlant);
    }

    @Override
    public List<PlantDto> getAll() {
        return plantRepository.findAll().stream()
                .sorted(Comparator.comparing(Plant::getId).reversed())
                .map(DtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlantDto> getAllByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found for id " + userId));
        return user.getPlantList().stream()
                .sorted(Comparator.comparing(Plant::getId).reversed())
                .map(DtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlantDto> getAllByUserFiltered(Long userId, String name, Long speciesId, String location) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found for id " + userId));
        Set<PlantDto> plants = new HashSet<>();
        Set<PlantDto> plantsAllByUser = new HashSet<>(user.getPlantList().stream()
                .map(DtoMapper::mapToDto)
                .collect(Collectors.toList()));
        boolean wasInIf = false;
        if(name==null && speciesId==null && location==null) {
            plants.addAll(user.getPlantList().stream()
                    .sorted(Comparator.comparing(Plant::getId).reversed())
                    .map(DtoMapper::mapToDto)
                    .collect(Collectors.toList()));
            wasInIf = true;
        }
        if(name!=null){
            List<PlantDto> tempPlants = plantsAllByUser.stream().filter(p -> p.getName().contains(name)).collect(Collectors.toList());
            plants = getPlantsDtos(plants, tempPlants, wasInIf);
            wasInIf =true;
        }
        if(speciesId!= null){
            List<PlantDto> tempPlants = plantsAllByUser.stream().filter(p -> p.getSpecies().getId().equals(speciesId)).collect(Collectors.toList());
            plants = getPlantsDtos(plants, tempPlants, wasInIf);
            wasInIf =true;
        }
        if(location!= null){
            List<PlantDto> tempPlants = plantsAllByUser.stream().filter(p -> p.getLocation().equals(location)).collect(Collectors.toList());
            plants = getPlantsDtos(plants, tempPlants, wasInIf);
        }
        return plants.stream()
                .sorted(Comparator.comparing(PlantDto::getId).reversed())
                .collect(Collectors.toList());
    }

    private Set<PlantDto> getPlantsDtos(Set<PlantDto> plants, List<PlantDto> temp, boolean was) {
        if (!plants.isEmpty()) {
            Set<Long> indexes = temp.stream().map(PlantDto::getId).collect(Collectors.toSet());
            plants = plants.stream().filter(p -> indexes.contains(p.getId())).collect(Collectors.toSet());
        }
        else if(was){
            plants = new HashSet<>();
        }
        else plants.addAll(temp);
        return plants;
    }

    @Override
    public PlantDto getById(Long id) {
        Plant plant = plantRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Plant not found for id " + id));
        return mapToDto(plant);
    }

    @Override
    public Set<String> getAllLocationsByUser(Long userId) {
        List<PlantDto> plantList;
        plantList = getAllByUser(userId);
        Set<String> location = new HashSet<>();
        for (PlantDto plantDto : plantList) {
            if (plantDto.getLocation() != null) {
                location.add(plantDto.getLocation());
            }
        }
        return location;
    }

    @Override
    public PlantDto update(Long id, PlantDto plantDto) {
        Plant plant = plantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Plant not found for id " + id));
        final String[] userHour = {"12:00:00"};
        ofNullable(plant.getUser()).ifPresent(u -> {
            userHour[0] = plant.getUser().getHourOfNotifications();
        });
        ofNullable(plantDto.getName())
                .ifPresent(p -> plant.setName(plantDto.getName()));
        ofNullable(plantDto.getLocation())
                .ifPresent(p -> plant.setLocation(plantDto.getLocation()));
        ofNullable(plantDto.getPhoto())
                .ifPresent(p -> plant.setPhoto(plantDto.getPhoto()));
        ofNullable(plantDto.getCategoryId())
                .ifPresent(c -> {throw new ImmutableFieldException("Field Category in Plant is immutable!");});
        ofNullable(plantDto.getSpecies())
                .ifPresent(s -> {
                   Species species = speciesRepository.findById(plantDto.getSpecies().getId()).orElseThrow(EntityNotFoundException::new);
                   plant.setSpecies(species);
                   if(!plantDto.getSpecies().getName().equals("Brak"))
                       plant.setCategory(species.getCategory());
                });
        ofNullable(plantDto.getUserId())
                .ifPresent(u -> {throw new ImmutableFieldException("Field User in Plant is immutable!");});
        ofNullable(plantDto.getPurchaseDate())
                .ifPresent(p -> plant.setPurchaseDate(plantDto.getPurchaseDate()));

        for (Event event: eventRepository.findAll()
                .stream().filter(e -> e.getPlant().getId().equals(plant.getId()))
                .filter(e -> !e.isDone())
                .collect(Collectors.toList())) {
            eventRepository.delete(event);
        }
        changeNameOfArchiveEvents(plant, WATERING, WATERING_PLANT);
        changeNameOfArchiveEvents(plant, MISTING, MISTING_PLANT);
        changeNameOfArchiveEvents(plant, FERTILIZING, FERTILIZING_PLANT);

        final Event[] eventW = {new Event()};
        final Event[] eventF = {new Event()};
        final Event[] eventM = {new Event()};
        ofNullable(plantDto.getSchedule()).ifPresent(s -> {
            if(plantDto.getSchedule().getWateringFrequency()>0){
                eventW[0] = addNewEvent(plant,  WATERING, WATERING_PLANT, plantDto.getSchedule().getWateringFrequency(), userHour[0]);
            }
            if(plantDto.getSchedule().getMistingFrequency()>0){
                eventM[0] = addNewEvent(plant, MISTING, MISTING_PLANT, plantDto.getSchedule().getMistingFrequency(), userHour[0]);
            }
            if(plantDto.getSchedule().getFertilizingFrequency()>0){
                eventF[0] = addNewEvent(plant, FERTILIZING, FERTILIZING_PLANT, plantDto.getSchedule().getFertilizingFrequency(), userHour[0]);
            }
        });

        beanValidator.validate(plant);
        Plant savedPlant = plantRepository.save(plant);
        if(plantDto.getSchedule().getWateringFrequency()>0)
            eventRepository.save(eventW[0]);
        if(plantDto.getSchedule().getMistingFrequency()>0)
            eventRepository.save(eventM[0]);
        if(plantDto.getSchedule().getFertilizingFrequency()>0)
            eventRepository.save(eventF[0]);
        return mapToDto(savedPlant);
    }

    private void changeNameOfArchiveEvents(Plant plant, String name, String description) {
        eventRepository.findAll()
                .stream().filter(e -> e.getPlant().getId().equals(plant.getId()))
                .filter(Event::isDone)
                .filter(e -> e.getEventName().contains(name))
                .forEach(e -> e.setEventDescription(description + plant.getName()));
    }

    private Event addNewEvent(Plant plant, String name, String longName, int days, String hour) {
        Event event = new Event();
        System.out.println(Integer.parseInt(hour.substring(0,1)));
        event.setDone(false);
        event.setEventDate(LocalDateTime.now()
                .withHour(Integer.parseInt(hour.substring(0,2)))
                .withMinute(Integer.parseInt(hour.substring(3,5)))
                .withSecond(Integer.parseInt(hour.substring(6,8)))
                .plusDays(days));
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
