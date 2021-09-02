package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.SpeciesDto;
import com.hepiplant.backend.entity.Schedule;
import com.hepiplant.backend.entity.Species;
import com.hepiplant.backend.repository.CategoryRepository;
import com.hepiplant.backend.repository.SpeciesRepository;
import com.hepiplant.backend.validator.BeanValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpeciesServiceImplTest {

    @Mock
    private SpeciesRepository speciesRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BeanValidator beanValidator;

    @InjectMocks
    private SpeciesServiceImpl speciesService;

    @Test
    void shouldGetAllOk() {
        //given
        Species species = new Species(1L, "species 1", 5, 21, 0, null, "soil 1", null);
        given(speciesRepository.findAll()).willReturn(List.of(species));

        //when
        List<SpeciesDto> result = speciesService.getAll();

        //then
        then(speciesRepository).should(times(1)).findAll();

        assertEquals(1,result.size());
        assertEquals("species 1", result.get(0).getName());
        assertEquals(5,result.get(0).getWateringFrequency());
        assertEquals(21,result.get(0).getFertilizingFrequency());
        assertEquals(0,result.get(0).getMistingFrequency());
        assertEquals("soil 1",result.get(0).getSoil());

    }

    @Test
    void shouldGetByIdOk() {
        //given
        Species species = new Species(1L, "species 1", 5, 21, 0, null, "soil 1", null);
        given(speciesRepository.findById(1L)).willReturn(java.util.Optional.of(species));

        //when
        SpeciesDto result = speciesService.getById(1L);

        //then
        then(speciesRepository).should(times(1)).findById(1L);

        assertEquals("species 1", result.getName());
        assertEquals(5,result.getWateringFrequency());
        assertEquals(21,result.getFertilizingFrequency());
        assertEquals(0,result.getMistingFrequency());
        assertEquals("soil 1",result.getSoil());
    }

    @Test
    void shouldAddOk() {
        //given
        Species species = new Species(1L, "species 1", 5, 21, 0, null, "soil 1", null);

        //when
        when(speciesRepository.save(any(Species.class))).thenAnswer(i -> i.getArguments()[0]);
        SpeciesDto speciesDto = new SpeciesDto();
        speciesDto.setName(species.getName());
        speciesDto.setWateringFrequency(species.getWateringFrequency());
        speciesDto.setFertilizingFrequency(species.getFertilizingFrequency());
        speciesDto.setMistingFrequency(species.getMistingFrequency());
        speciesDto.setSoil(species.getSoil());
        SpeciesDto result = speciesService.add(speciesDto);

        //then
        then(speciesRepository).should(times(1)).save(any(Species.class));

        assertEquals("species 1", result.getName());
        assertEquals(5,result.getWateringFrequency());
        assertEquals(21,result.getFertilizingFrequency());
        assertEquals(0,result.getMistingFrequency());
        assertEquals("soil 1",result.getSoil());
    }

    @Test
    void shouldUpdateOk() {
    }

    @Test
    void shouldDeleteOk() {
    }
}