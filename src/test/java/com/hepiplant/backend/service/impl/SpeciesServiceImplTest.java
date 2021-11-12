package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.SpeciesDto;
import com.hepiplant.backend.entity.Category;
import com.hepiplant.backend.entity.Species;
import com.hepiplant.backend.entity.enums.Placement;
import com.hepiplant.backend.exception.ImmutableFieldException;
import com.hepiplant.backend.exception.InvalidBeanException;
import com.hepiplant.backend.repository.CategoryRepository;
import com.hepiplant.backend.repository.SpeciesRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpeciesServiceImplTest {

    @Mock
    private SpeciesRepository speciesRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BeanValidator beanValidator;

    @Captor
    private ArgumentCaptor<Species> speciesArgumentCaptor;

    @InjectMocks
    private SpeciesServiceImpl speciesService;
    private Species species;
    private SpeciesDto dto;
    private static Category category;

    @BeforeAll
    public static void initializeVariables(){
        category = new Category(1L,"name",null);
    }

    @BeforeEach
    public void initializeSpecies(){

        species = new Species(1L, "species 1", 5, 21, 0, Placement.BARDZO_JASNE, "soil 1", category);
        species.setPlantList(new ArrayList<>());
        dto = new SpeciesDto();
        dto.setName(species.getName());
        dto.setWateringFrequency(species.getWateringFrequency());
        dto.setFertilizingFrequency(species.getFertilizingFrequency());
        dto.setMistingFrequency(species.getMistingFrequency());
        dto.setSoil(species.getSoil());
        dto.setCategoryId(species.getCategory().getId());
        dto.setPlacement(species.getPlacement());
    }

    @Test
    void shouldGetAllSpeciesOk() {
        //given
        given(speciesRepository.findAll()).willReturn(List.of(species));

        //when
        List<SpeciesDto> result = speciesService.getAll();

        //then
        then(speciesRepository).should(times(1)).findAll();

        assertEquals(1,result.size());
        assertEquals(species.getName(), result.get(0).getName());
        assertEquals(species.getWateringFrequency(),result.get(0).getWateringFrequency());
        assertEquals(species.getFertilizingFrequency(),result.get(0).getFertilizingFrequency());
        assertEquals(species.getMistingFrequency(),result.get(0).getMistingFrequency());
        assertEquals(species.getSoil(),result.get(0).getSoil());
        assertEquals(species.getPlacement(), result.get(0).getPlacement());
        assertEquals(species.getCategory().getId(), result.get(0).getCategoryId());

    }

    @Test
    public void shouldGetAllSpeciesEmptyListOk()
    {
        //given
        given(speciesRepository.findAll()).willReturn(List.of());

        //when
        List<SpeciesDto> result = speciesService.getAll();

        //then
        then(speciesRepository).should(times(1)).findAll();
        assertEquals(0, result.size());
    }

    @Test
    void shouldGetSpeciesByIdOk() {
        //given
        given(speciesRepository.findById(species.getId())).willReturn(java.util.Optional.of(species));

        //when
        SpeciesDto result = speciesService.getById(species.getId());

        //then
        then(speciesRepository).should(times(1)).findById(species.getId());

        assertEquals(species.getName(), result.getName());
        assertEquals(species.getWateringFrequency(),result.getWateringFrequency());
        assertEquals(species.getFertilizingFrequency(),result.getFertilizingFrequency());
        assertEquals(species.getMistingFrequency(),result.getMistingFrequency());
        assertEquals(species.getSoil(),result.getSoil());
        assertEquals(species.getPlacement(), result.getPlacement());
        assertEquals(species.getCategory().getId(), result.getCategoryId());
    }

    @Test
    void shouldGetSpeciesByIdDoesNotExistThrowsException(){

        //given
        given(speciesRepository.findById(species.getId())).willReturn(Optional.empty());

        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> speciesService.getById(species.getId()));
        then(speciesRepository).should(times(1)).findById(species.getId());

    }

    @Test
    void shouldAddSpeciesOk() {
        //given
        given(categoryRepository.findById(species.getCategory().getId())).willReturn(Optional.ofNullable(category));
        given(speciesRepository.save(speciesArgumentCaptor.capture())).willAnswer(returnsFirstArg());
        //when
        SpeciesDto result = speciesService.add(dto);
        //then
        then(categoryRepository).should(times(1)).findById(dto.getCategoryId());
        then(beanValidator).should(times(1)).validate(any());
        then(speciesRepository).should(times(1)).save(any(Species.class));

        assertEquals(species.getName(), result.getName());
        assertEquals(species.getWateringFrequency(),result.getWateringFrequency());
        assertEquals(species.getFertilizingFrequency(),result.getFertilizingFrequency());
        assertEquals(species.getMistingFrequency(),result.getMistingFrequency());
        assertEquals(species.getSoil(),result.getSoil());
        assertEquals(species.getPlacement(), result.getPlacement());
        assertEquals(species.getCategory().getId(), result.getCategoryId());

        Species captorValue = speciesArgumentCaptor.getValue();
        assertEquals(species.getName(), captorValue.getName());
        assertEquals(species.getWateringFrequency(),captorValue.getWateringFrequency());
        assertEquals(species.getFertilizingFrequency(),captorValue.getFertilizingFrequency());
        assertEquals(species.getMistingFrequency(),captorValue.getMistingFrequency());
        assertEquals(species.getSoil(),captorValue.getSoil());
        assertEquals(species.getPlacement(), captorValue.getPlacement());
        assertEquals(category, captorValue.getCategory());
    }

    @Test
    void shouldAddSpeciesInvalidValuesThrowsException() {
        //given
        given(categoryRepository.findById(dto.getCategoryId())).willReturn(Optional.of(category));
        doThrow(InvalidBeanException.class).when(beanValidator).validate(any());

        //when
        //then
        assertThrows(InvalidBeanException.class, () -> speciesService.add(dto));
        then(categoryRepository).should(times(1)).findById(eq(dto.getCategoryId()));
        then(beanValidator).should(times(1)).validate(any());
        then(speciesRepository).should(times(0)).save(any(Species.class));
    }

    @Test
    void shouldAddSpeciesCategoryDoesNotExistsThrowsException() {
        //given
        given(categoryRepository.findById(dto.getCategoryId())).willReturn(Optional.empty());

        //when
        //then
        assertThrows(EntityNotFoundException.class, () -> speciesService.add(dto));
        then(categoryRepository).should(times(1)).findById(eq(dto.getCategoryId()));
        then(speciesRepository).should(times(0)).save(any(Species.class));
    }

    @Test
    void shouldUpdateSpeciesOk() {
        //given
        Species speciesToUpdate = new Species();
        given(speciesRepository.findById(species.getId())).willReturn(Optional.of(speciesToUpdate));
        given(speciesRepository.save(speciesArgumentCaptor.capture())).willAnswer(returnsFirstArg());
        given(categoryRepository.findById(dto.getCategoryId())).willReturn(Optional.of(category));

        //when
        SpeciesDto result = speciesService.update(species.getId(), dto);

        //then
        then(speciesRepository).should(times(1)).findById(species.getId());
        then(categoryRepository).should(times(1)).findById(eq(dto.getCategoryId()));
        then(beanValidator).should(times(1)).validate(any());
        then(speciesRepository).should(times(1)).save(any(Species.class));
        assertEquals(species.getName(), result.getName());
        assertEquals(species.getWateringFrequency(),result.getWateringFrequency());
        assertEquals(species.getFertilizingFrequency(),result.getFertilizingFrequency());
        assertEquals(species.getMistingFrequency(),result.getMistingFrequency());
        assertEquals(species.getPlacement(), result.getPlacement());
        assertEquals(species.getSoil(), result.getSoil());
        assertEquals(species.getCategory().getId(), result.getCategoryId());

        Species captorValue = speciesArgumentCaptor.getValue();
        assertEquals(species.getName(), captorValue.getName());
        assertEquals(species.getWateringFrequency(),captorValue.getWateringFrequency());
        assertEquals(species.getFertilizingFrequency(),captorValue.getFertilizingFrequency());
        assertEquals(species.getMistingFrequency(),captorValue.getMistingFrequency());
        assertEquals(species.getPlacement(), captorValue.getPlacement());
        assertEquals(species.getSoil(), captorValue.getSoil());
        assertEquals(species.getCategory(), captorValue.getCategory());
    }

    @Test
    public void shouldUpdateSpeciesInvalidValuesThrowsException()
    {
        //given
        Species speciesToUpdate = new Species();
        dto.setSoil(null);
        dto.setCategoryId(null);
        given(speciesRepository.findById(species.getId())).willReturn(Optional.of(speciesToUpdate));
        doThrow(InvalidBeanException.class).when(beanValidator).validate(any());

        //when

        //then
        assertThrows(InvalidBeanException.class, () -> speciesService.update(species.getId(), dto));
        then(speciesRepository).should(times(1)).findById(species.getId());
        then(beanValidator).should(times(1)).validate(any());
        then(speciesRepository).should(times(0)).save(any(Species.class));
    }

    @Test
    void shouldDeleteSpeciesOk() {
        //given
        given(speciesRepository.findById(species.getId())).willReturn(java.util.Optional.of(species));

        //when
        String result = speciesService.delete(species.getId());

        //then
        then(speciesRepository).should(times(1)).findById(species.getId());
        then(speciesRepository).should(times(1)).delete(species);
        assertTrue(result.contains("Successfully deleted") );
    }

    @Test
    void shouldDeleteSpeciesDoesNotExistThrowsException() {
        //given
        given(speciesRepository.findById(species.getId())).willReturn(Optional.empty());

        //when
        String result = speciesService.delete(species.getId());

        //then
        then(speciesRepository).should(times(1)).findById(species.getId());
        then(speciesRepository).should(times(0)).delete(any());
        assertTrue(result.contains("No species") );
    }

}
