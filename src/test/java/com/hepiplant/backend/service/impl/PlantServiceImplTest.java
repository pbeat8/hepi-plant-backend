package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.PlantDto;
import com.hepiplant.backend.entity.*;
import com.hepiplant.backend.entity.enums.Permission;
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
import java.util.Optional;

import static com.hepiplant.backend.entity.enums.Placement.BARDZO_JASNE;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.times;

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
    private static Species species;
    private static User user;

    @BeforeAll
    public static void initializeVariables(){
        user = new User(1L, "username1", "login1", "p@ssw0rd", "email@gmail.com",
                Permission.USER, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        category = new Category(2L, "Category1", new ArrayList<>());
        species = new Species(3L, "name", 3, 21,1,  BARDZO_JASNE, "soil", category);
    }

    @BeforeEach
    public void initializePlant(){
        plant = new Plant(1l,"name",null,"location",category,species,user,null,null);
        dto = new PlantDto();
        dto.setId(plant.getId());
        dto.setName(plant.getName());
        dto.setPurchaseDate(plant.getPurchaseDate());
        dto.setLocation(plant.getLocation());
        dto.setCategoryId(category.getId());
        dto.setSpeciesId(user.getId());
        dto.setUserId(species.getId());
    }

    //CREATE tests
    @Test
    public void shouldCreatePlantOk(){
        //given
        given(categoryRepository.findById(dto.getCategoryId())).willReturn(Optional.of(category));
        given(speciesRepository.findById(dto.getSpeciesId())).willReturn(Optional.of(species));
        given(userRepository.findById(dto.getUserId())).willReturn(Optional.of(user));
        given(plantRepository.save(plantArgumentCaptor.capture())).willAnswer(returnsFirstArg());

        //when
        PlantDto result = plantService.create(dto);

        //then
        then(categoryRepository).should(times(1)).findById(eq(dto.getCategoryId()));
        then(speciesRepository).should(times(1)).findById(eq(dto.getSpeciesId()));
        then(userRepository).should(times(1)).findById(eq(dto.getUserId()));
        then(beanValidator).should(times(1)).validate(any());
        then(plantRepository).should(times(1)).save(any(Plant.class));
        assertEquals(plant.getName(), result.getName());
        assertEquals(plant.getPurchaseDate(), result.getPurchaseDate());
        assertEquals(plant.getLocation(), result.getLocation());
        assertEquals(plant.getCategory().getId(), result.getCategoryId());
        assertEquals(plant.getSpecies().getId(), result.getSpeciesId());
        assertEquals(plant.getUser().getId(), result.getUserId());

        Plant captorValue = plantArgumentCaptor.getValue();
        assertEquals(plant.getName(), captorValue.getName());
        assertEquals(plant.getPurchaseDate(), captorValue.getPurchaseDate());
        assertEquals(plant.getLocation(), captorValue.getLocation());
        assertEquals(category, captorValue.getCategory());
        assertEquals(species, captorValue.getSpecies());
        assertEquals(user, captorValue.getUser());

    }
    @Test
    public void shouldCreatePlantCategoryDoesNotExistThrowsException(){
        //given
        given(categoryRepository.findById(dto.getCategoryId())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> plantService.create(dto));
        then(categoryRepository).should(times(1)).findById(eq(dto.getCategoryId()));
        then(userRepository).should(atMostOnce()).findById(eq(dto.getUserId()));
        then(speciesRepository).should(atMostOnce()).findById(eq(dto.getSpeciesId()));
        then(plantRepository).should(times(0)).save(any(Plant.class));
    }
}
