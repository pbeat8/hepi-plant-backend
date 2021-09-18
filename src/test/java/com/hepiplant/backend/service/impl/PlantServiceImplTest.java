package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.PlantDto;
import com.hepiplant.backend.entity.*;
import com.hepiplant.backend.entity.enums.Permission;
import com.hepiplant.backend.exception.ImmutableFieldException;
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
    private static Schedule schedule;
    private static User user;
    private static User user2;

    @BeforeAll
    public static void initializeVariables(){
        Plant plant2 = new Plant(1l,"name",null,"location",null,null,null,null,null);
        ArrayList<Plant> plants = new ArrayList<Plant>();
        plants.add(plant2);
        user = new User(1L, "username1", "login1", "p@ssw0rd", "email@gmail.com",
                Permission.USER, plants, new ArrayList<>(), new ArrayList<>());
        user2 = new User(2L, "username2", "login2", "p@ssw0rd2", "email2@gmail.com",
                Permission.USER, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        category = new Category(2L, "Category1", new ArrayList<>());
        category2 = new Category(4L, "Category2", new ArrayList<>());
        species = new Species(3L, "name", 3, 21,1,  BARDZO_JASNE, "soil", category);
        schedule = new Schedule(1L, plant2, 2, 21, 2);
    }

    @BeforeEach
    public void initializePlant(){
        plant = new Plant(1l,"name",null,"location",category,species,user,null,schedule);
        dto = new PlantDto();
        dto.setId(plant.getId());
        dto.setName(plant.getName());
        dto.setPurchaseDate(plant.getPurchaseDate());
        dto.setLocation(plant.getLocation());
        dto.setCategoryId(category.getId());
        dto.setSpeciesId(species.getId());
        dto.setUserId(user.getId());

    }

    //CREATE tests
    @Test
    public void shouldCreatePlantOk(){
        //given
        given(speciesRepository.findById(dto.getSpeciesId())).willReturn(Optional.of(species));
        given(userRepository.findById(dto.getUserId())).willReturn(Optional.of(user));
        given(plantRepository.save(plantArgumentCaptor.capture())).willAnswer(returnsFirstArg());

        //when
        PlantDto result = plantService.create(dto);

        //then
        then(speciesRepository).should(times(1)).findById(eq(dto.getSpeciesId()));
        then(userRepository).should(times(1)).findById(eq(dto.getUserId()));
        then(beanValidator).should(times(1)).validate(any());
        then(plantRepository).should(times(1)).save(any(Plant.class));
        assertEquals(plant.getName(), result.getName());
        assertEquals(plant.getPurchaseDate(), result.getPurchaseDate());
        assertEquals(plant.getLocation(), result.getLocation());
        assertEquals(category.getId(), result.getCategoryId());
        assertEquals(species.getId(), result.getSpeciesId());
        assertEquals(user.getId(), result.getUserId());




        Plant captorValue = plantArgumentCaptor.getValue();
        assertEquals(plant.getName(), captorValue.getName());
        assertEquals(plant.getPurchaseDate(), captorValue.getPurchaseDate());
        assertEquals(plant.getLocation(), captorValue.getLocation());
        assertEquals(category, captorValue.getCategory());
        assertEquals(species, captorValue.getSpecies());
        assertEquals(user, captorValue.getUser());


    }
    @Test
    public void shouldCreateUserDoesNotExistThrowsException(){
        //given

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> plantService.create(dto));
        then(speciesRepository).should(atMostOnce()).findById(eq(dto.getSpeciesId()));
        then(plantRepository).should(times(0)).save(any(Plant.class));
    }
    @Test
    public void shouldCreateSpeciesDoesNotExistThrowsException(){
        //given

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> plantService.create(dto));
        then(userRepository).should(atMostOnce()).findById(eq(dto.getUserId()));
        then(plantRepository).should(times(0)).save(any(Plant.class));
    }
    @Test
    public void shouldCreatePlantInvalidValuesThrowsException() {
        //given
        given(speciesRepository.findById(dto.getSpeciesId())).willReturn(Optional.of(species));
        given(userRepository.findById(dto.getUserId())).willReturn(Optional.of(user));
        doThrow(InvalidBeanException.class).when(beanValidator).validate(any());

        //when

        //then
        assertThrows(InvalidBeanException.class, () -> plantService.create(dto));
        then(userRepository).should(times(1)).findById(eq(dto.getUserId()));
        then(speciesRepository).should(times(1)).findById(eq(dto.getSpeciesId()));
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
        assertEquals(category.getId(), result.get(0).getCategoryId());
        assertEquals(species.getId(), result.get(0).getSpeciesId());
        assertEquals(user.getId(), result.get(0).getUserId());


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
        assertEquals(category.getId(), result.getCategoryId());
        assertEquals(species.getId(), result.getSpeciesId());
        assertEquals(user.getId(), result.getUserId());
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
        given(speciesRepository.findById(dto.getSpeciesId())).willReturn(Optional.of(species));
        given(plantRepository.save(plantArgumentCaptor.capture())).willAnswer(returnsFirstArg());

        //when
        PlantDto result = plantService.update(plant.getId(), dto);

        //then
        then(plantRepository).should(times(1)).findById(plant.getId());
        then(beanValidator).should(times(1)).validate(any());
        then(plantRepository).should(times(1)).save(any(Plant.class));
        assertEquals(plant.getName(), result.getName());
        assertEquals(plant.getPurchaseDate(), result.getPurchaseDate());
        assertEquals(plant.getLocation(), result.getLocation());
        assertEquals(category.getId(), result.getCategoryId());
        assertEquals(species.getId(), result.getSpeciesId());

        Plant captorValue = plantArgumentCaptor.getValue();
        assertEquals(plant.getName(), captorValue.getName());
        assertEquals(plant.getPurchaseDate(), captorValue.getPurchaseDate());
        assertEquals(plant.getLocation(), captorValue.getLocation());
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
        given(speciesRepository.findById(dto.getSpeciesId())).willReturn(Optional.of(species));
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
