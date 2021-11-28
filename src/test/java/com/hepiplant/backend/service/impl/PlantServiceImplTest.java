package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.PlantDto;
import com.hepiplant.backend.dto.ScheduleDto;
import com.hepiplant.backend.dto.SpeciesDto;
import com.hepiplant.backend.entity.*;
import com.hepiplant.backend.exception.InvalidBeanException;
import com.hepiplant.backend.repository.*;
import com.hepiplant.backend.validator.BeanValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.hepiplant.backend.entity.enums.Placement.BARDZO_JASNE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlantServiceImplTest {

    @Mock
    private PlantRepository plantRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private SpeciesRepository speciesRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private BeanValidator beanValidator;

    @Captor
    private ArgumentCaptor<Plant> plantArgumentCaptor;

    @InjectMocks
    private PlantServiceImpl plantService;

    private Plant plant;
    private PlantDto dto;
    private static Category category;
    private static Category category2;
    private static Species species;
    private static SpeciesDto speciesDto;
    private static Schedule schedule;
    private static ScheduleDto scheduleDto;
    private static User user;
    private static User user2;

    @BeforeAll
    public static void initializeVariables(){
        Plant plant2 = new Plant(1l,"name",null,"location", null, null,null,null,null,null);
        ArrayList<Plant> plants = new ArrayList<Plant>();
        plants.add(plant2);
        user = new User(1L, "username1", "p@ssw0rd", "email@gmail.com",
                true, "00:00:00", null, plants, new ArrayList<>(), new ArrayList<>());
        user2 = new User(2L, "username2", "p@ssw0rd2", "email2@gmail.com",
                true, "00:00:00", null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        category = new Category(2L, "Category1", new ArrayList<>());
        category2 = new Category(4L, "Category2", new ArrayList<>());
        species = new Species(3L, "name", 3, 21, 1, BARDZO_JASNE, "soil", category);
        speciesDto = new SpeciesDto();
        speciesDto.setId(species.getId());
        speciesDto.setName(species.getName());
        speciesDto.setWateringFrequency(species.getWateringFrequency());
        speciesDto.setFertilizingFrequency(species.getFertilizingFrequency());
        speciesDto.setMistingFrequency(species.getMistingFrequency());
        speciesDto.setPlacement(species.getPlacement());
        speciesDto.setSoil(species.getSoil());
        speciesDto.setCategoryId(species.getCategory().getId());
        schedule = new Schedule(5L, plant2, 2, 21, 2);
        scheduleDto = new ScheduleDto();
        scheduleDto.setId(5L);
        scheduleDto.setPlantId(plant2.getId());
        scheduleDto.setWateringFrequency(schedule.getWateringFrequency());
        scheduleDto.setFertilizingFrequency(schedule.getFertilizingFrequency());
        scheduleDto.setMistingFrequency(schedule.getMistingFrequency());
    }

    @BeforeEach
    public void initializePlant(){
        plant = new Plant(1l,"name",null,"location", null, category,species,user,null,schedule);
        dto = new PlantDto();
        dto.setId(plant.getId());
        dto.setName(plant.getName());
        dto.setPurchaseDate(plant.getPurchaseDate());
        dto.setLocation(plant.getLocation());
        dto.setPhoto(plant.getPhoto());
        dto.setCategoryId(category.getId());
        dto.setSpecies(speciesDto);
        dto.setUserId(user.getId());
        dto.setSchedule(scheduleDto);

    }

    //CREATE tests
    @Test
    public void shouldCreatePlantOk(){
        //given
        given(speciesRepository.findById(dto.getSpecies().getId())).willReturn(Optional.of(species));
        given(userRepository.findById(dto.getUserId())).willReturn(Optional.of(user));
        given(scheduleRepository.save(any())).willAnswer(returnsFirstArg());
        given(eventRepository.save(any())).willAnswer(returnsFirstArg());
        given(plantRepository.save(plantArgumentCaptor.capture())).willAnswer(returnsFirstArg());

        //when
        PlantDto result = plantService.create(dto);

        //then
        then(speciesRepository).should(times(1)).findById(eq(dto.getSpecies().getId()));
        then(scheduleRepository).should(times(1)).save(any(Schedule.class));
        then(eventRepository).should(times(3)).save(any(Event.class));
        then(userRepository).should(times(1)).findById(eq(dto.getUserId()));
        then(beanValidator).should(times(1)).validate(any());
        then(plantRepository).should(times(1)).save(any(Plant.class));
        assertEquals(plant.getName(), result.getName());
        assertEquals(plant.getPurchaseDate(), result.getPurchaseDate());
        assertEquals(plant.getLocation(), result.getLocation());
        assertEquals(plant.getPhoto(), result.getPhoto());
        assertEquals(category.getId(), result.getCategoryId());
        assertEquals(species.getId(), result.getSpecies().getId());
        assertEquals(user.getId(), result.getUserId());


        Plant captorValue = plantArgumentCaptor.getValue();
        assertEquals(plant.getName(), captorValue.getName());
        assertEquals(plant.getPurchaseDate(), captorValue.getPurchaseDate());
        assertEquals(plant.getLocation(), captorValue.getLocation());
        assertEquals(plant.getPhoto(), captorValue.getPhoto());
        assertEquals(category, captorValue.getCategory());
        assertEquals(species, captorValue.getSpecies());
        assertEquals(user, captorValue.getUser());
        assertEquals(species.getFertilizingFrequency(), captorValue.getSchedule().getFertilizingFrequency());
        assertEquals(scheduleDto.getWateringFrequency(), captorValue.getSchedule().getWateringFrequency());
        assertEquals(species.getMistingFrequency(), captorValue.getSchedule().getMistingFrequency());
    }

    @Test
    public void shouldCreateUserDoesNotExistThrowsException(){
        //given

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> plantService.create(dto));
        then(speciesRepository).should(atMostOnce()).findById(eq(dto.getSpecies().getId()));
        then(scheduleRepository).should(atMostOnce()).save(any(Schedule.class));
        then(plantRepository).should(times(0)).save(any(Plant.class));
    }

    @Test
    public void shouldCreateSpeciesDoesNotExistThrowsException(){
        //given

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> plantService.create(dto));
        then(userRepository).should(atMostOnce()).findById(eq(dto.getUserId()));
        then(scheduleRepository).should(atMostOnce()).save(any(Schedule.class));
        then(plantRepository).should(times(0)).save(any(Plant.class));
    }

    @Test
    public void shouldCreateScheduleDoesNotExistThrowsException(){
        //given

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> plantService.create(dto));
        then(userRepository).should(atMostOnce()).findById(eq(dto.getUserId()));
        then(speciesRepository).should(atMostOnce()).findById(eq(dto.getSpecies().getId()));
        then(plantRepository).should(times(0)).save(any(Plant.class));
    }

    @Test
    public void shouldCreatePlantInvalidValuesThrowsException() {
        //given
        given(speciesRepository.findById(dto.getSpecies().getId())).willReturn(Optional.of(species));
        given(userRepository.findById(dto.getUserId())).willReturn(Optional.of(user));
        doThrow(InvalidBeanException.class).when(beanValidator).validate(any());

        //when

        //then
        assertThrows(InvalidBeanException.class, () -> plantService.create(dto));
        then(userRepository).should(times(1)).findById(eq(dto.getUserId()));
        then(speciesRepository).should(times(1)).findById(eq(dto.getSpecies().getId()));
        then(beanValidator).should(times(1)).validate(any());
        then(plantRepository).should(times(0)).save(any(Plant.class));
    }

    // GET ALL tests

    @Test
    public void shouldGetAllPlantsOk(){
        //given
        given(plantRepository.findAll()).willReturn(List.of(plant));

        //when
        List<PlantDto> result = plantService.getAll();

        //then
        then(plantRepository).should(times(1)).findAll();
        assertEquals(1, result.size());
        assertEquals(plant.getName(), result.get(0).getName());
        assertEquals(plant.getPurchaseDate(), result.get(0).getPurchaseDate());
        assertEquals(plant.getLocation(), result.get(0).getLocation());
        assertEquals(plant.getPhoto(), result.get(0).getPhoto());
        assertEquals(category.getId(), result.get(0).getCategoryId());
        assertEquals(species.getId(), result.get(0).getSpecies().getId());
        assertEquals(user.getId(), result.get(0).getUserId());
        assertEquals(scheduleDto.getFertilizingFrequency(), result.get(0).getSchedule().getFertilizingFrequency());
        assertEquals(scheduleDto.getWateringFrequency(), result.get(0).getSchedule().getWateringFrequency());
        assertEquals(scheduleDto.getMistingFrequency(), result.get(0).getSchedule().getMistingFrequency());
    }

    @Test
    public void shouldGetAllPlantEmptyListOk(){
        //given
        given(plantRepository.findAll()).willReturn(List.of());

        //when
        List<PlantDto> result = plantService.getAll();

        //then
        then(plantRepository).should(times(1)).findAll();
        assertEquals(0, result.size());
    }
    // GET ALL BY User tests

    @Test
    public void shouldGetAllPlantsByUserOk() {
        //given
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        //when
        List<PlantDto> result = plantService.getAllByUser(1L);

        //then
        then(userRepository).should(times(1)).findById(user.getId());
        assertEquals(1, result.size());
    }

    //GET by id

    @Test
    public void shouldGetPlantByIdOk() {
        //given
        given(plantRepository.findById(plant.getId())).willReturn(Optional.of(plant));

        //when
        PlantDto result = plantService.getById(plant.getId());

        //then
        then(plantRepository).should(times(1)).findById(plant.getId());
        assertEquals(plant.getName(), result.getName());
        assertEquals(plant.getPurchaseDate(), result.getPurchaseDate());
        assertEquals(plant.getLocation(), result.getLocation());
        assertEquals(plant.getPhoto(), result.getPhoto());
        assertEquals(category.getId(), result.getCategoryId());
        assertEquals(species.getId(), result.getSpecies().getId());
        assertEquals(user.getId(), result.getUserId());
        assertEquals(scheduleDto.getFertilizingFrequency(), result.getSchedule().getFertilizingFrequency());
        assertEquals(scheduleDto.getWateringFrequency(), result.getSchedule().getWateringFrequency());
        assertEquals(scheduleDto.getMistingFrequency(), result.getSchedule().getMistingFrequency());
    }

    @Test
    public void shouldGetPlantByIdPostDoesNotExistThrowsException(){
        //given
        given(plantRepository.findById(plant.getId())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> plantService.getById(plant.getId()));
        then(plantRepository).should(times(1)).findById(plant.getId());
    }

    // UPDATE tests
    @Test
    public void shouldUpdatePlantOk() {
        //given
        Plant plantToUpdate = new Plant();
        dto.setUserId(null);
        dto.setCategoryId(null);

        given(plantRepository.findById(plant.getId())).willReturn(Optional.of(plantToUpdate));
        given(speciesRepository.findById(dto.getSpecies().getId())).willReturn(Optional.of(species));
        given(eventRepository.findAll()).willReturn(List.of());
        given(eventRepository.save(any())).willAnswer(returnsFirstArg());
        given(plantRepository.save(plantArgumentCaptor.capture())).willAnswer(returnsFirstArg());

        //when
        PlantDto result = plantService.update(plant.getId(), dto);

        //then
        then(plantRepository).should(times(1)).findById(plant.getId());
        then(eventRepository).should(times(1)).findAll();
        then(eventRepository).should(times(3)).save(any(Event.class));
        then(beanValidator).should(times(1)).validate(any());
        then(plantRepository).should(times(1)).save(any(Plant.class));
        assertEquals(plant.getName(), result.getName());
        assertEquals(plant.getPurchaseDate(), result.getPurchaseDate());
        assertEquals(plant.getLocation(), result.getLocation());
        assertEquals(plant.getPhoto(), result.getPhoto());
        assertEquals(category.getId(), result.getCategoryId());
        assertEquals(species.getId(), result.getSpecies().getId());

        Plant captorValue = plantArgumentCaptor.getValue();
        assertEquals(plant.getName(), captorValue.getName());
        assertEquals(plant.getPurchaseDate(), captorValue.getPurchaseDate());
        assertEquals(plant.getLocation(), captorValue.getLocation());
        assertEquals(plant.getPhoto(), captorValue.getPhoto());
        assertEquals(category, captorValue.getCategory());
        assertEquals(species, captorValue.getSpecies());
    }

    @Test
    public void shouldUpdatePlantDoesNotExistThrowsException(){
        //given
        dto.setUserId(null);
        dto.setCategoryId(null);

        given(plantRepository.findById(plant.getId())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> plantService.update(plant.getId(), dto));
        then(plantRepository).should(times(1)).findById(plant.getId());
        then(plantRepository).should(times(0)).save(any(Plant.class));
    }

    @Test
    public void shouldUpdatePlantInvalidValuesThrowsException(){
        //given
        Plant plantToUpdate = new Plant();
        dto.setUserId(null);
        dto.setCategoryId(null);


        given(plantRepository.findById(plant.getId())).willReturn(Optional.of(plantToUpdate));
        given(speciesRepository.findById(dto.getSpecies().getId())).willReturn(Optional.of(species));
        doThrow(InvalidBeanException.class).when(beanValidator).validate(any());

        //when

        //then
        assertThrows(InvalidBeanException.class, () -> plantService.update(plant.getId(), dto));
        then(plantRepository).should(times(1)).findById(plant.getId());
        then(categoryRepository).should(atMostOnce()).findById(eq(dto.getCategoryId()));
        then(beanValidator).should(times(1)).validate(any());
        then(plantRepository).should(times(0)).save(any(Plant.class));
    }

    // DELETE tests
    @Test
    public void shouldDeletePlantOk(){
        //given
        given(plantRepository.findById(plant.getId())).willReturn(Optional.of(plant));

        //when
        String result = plantService.delete(plant.getId());

        //then
        then(plantRepository).should(times(1)).findById(plant.getId());
        then(plantRepository).should(times(1)).delete(plant);
        assertTrue(result.contains("Successfully deleted"));
    }

    @Test
    public void shouldDeletePlantDoesNotExistThrowsException(){
        //given
        given(plantRepository.findById(plant.getId())).willReturn(Optional.empty());

        //when
        String result = plantService.delete(plant.getId());

        //then
        then(plantRepository).should(times(1)).findById(plant.getId());
        then(plantRepository).should(times(0)).delete(any());
        assertTrue(result.contains("No plant"));
    }

}
