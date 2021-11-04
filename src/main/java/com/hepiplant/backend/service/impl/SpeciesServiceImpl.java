package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.PlantDto;
import com.hepiplant.backend.dto.SpeciesDto;
import com.hepiplant.backend.entity.Category;
import com.hepiplant.backend.entity.Species;
import com.hepiplant.backend.exception.ImmutableFieldException;
import com.hepiplant.backend.mapper.DtoMapper;
import com.hepiplant.backend.repository.CategoryRepository;
import com.hepiplant.backend.repository.SpeciesRepository;
import com.hepiplant.backend.service.PlantService;
import com.hepiplant.backend.service.SpeciesService;
import com.hepiplant.backend.validator.BeanValidator;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hepiplant.backend.mapper.DtoMapper.mapToDto;

@Service
public class SpeciesServiceImpl implements SpeciesService {

    private final SpeciesRepository speciesRepository;
    private final CategoryRepository categoryRepository;
    private final BeanValidator beanValidator;
    private final PlantService plantService;

    public SpeciesServiceImpl(SpeciesRepository speciesRepository, CategoryRepository categoryRepository, BeanValidator beanValidator, PlantService plantService) {

        this.speciesRepository = speciesRepository;
        this.categoryRepository = categoryRepository;
        this.beanValidator = beanValidator;
        this.plantService = plantService;
    }

    @Override
    public List<SpeciesDto> getAll() {
        return speciesRepository.findAll().stream()
                .sorted(Comparator.comparing(Species::getId))
                .map(DtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public SpeciesDto getById(Long id) {
        Species species = speciesRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Species not found for id "+id));
        return mapToDto(species);
    }

    @Override
    public SpeciesDto add(SpeciesDto speciesDto) {
        Species species = new Species();
        species.setName(speciesDto.getName());
        species.setWateringFrequency(speciesDto.getWateringFrequency());
        species.setFertilizingFrequency(speciesDto.getFertilizingFrequency());
        species.setMistingFrequency(speciesDto.getMistingFrequency());
        species.setPlacement(speciesDto.getPlacement());
        species.setSoil(speciesDto.getSoil());
        if(speciesDto.getCategoryId()!=null) {
            Category category = categoryRepository.findById(speciesDto.getCategoryId()).orElseThrow(() -> new EntityNotFoundException("Category not found for id " + speciesDto.getCategoryId()));
            species.setCategory(category);
        }
        beanValidator.validate(species);
        Species savedSpecies = speciesRepository.save(species);
        return mapToDto(savedSpecies);
    }

    @Override
    public SpeciesDto update(Long id, SpeciesDto speciesDto) {
        Species species = speciesRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Species not found for id "+id));
        if(speciesDto.getName()!=null)
            species.setName(speciesDto.getName());
        species.setWateringFrequency(speciesDto.getWateringFrequency());
        species.setFertilizingFrequency(speciesDto.getFertilizingFrequency());
        species.setMistingFrequency(speciesDto.getMistingFrequency());
        if(speciesDto.getPlacement()!=null)
            species.setPlacement(speciesDto.getPlacement());
        if(speciesDto.getSoil()!=null){
            species.setSoil(speciesDto.getSoil());
        }
        if(speciesDto.getCategoryId()!=null){
            Category category = categoryRepository.findById(speciesDto.getCategoryId()).orElseThrow(() -> new EntityNotFoundException("Category not found for id " + speciesDto.getCategoryId()));
            species.setCategory(category);
        }
        beanValidator.validate(species);
        Species savedSpecies = speciesRepository.save(species);
        return mapToDto(savedSpecies);
    }

    @Override
    public String delete(Long id) {
        Optional<Species> species = speciesRepository.findById(id);
        if(species.isEmpty()){
            return "No species with id = " + id;
        }
        Species speciesValue = species.get();
        speciesValue.getPlantList()
                .forEach(p -> p.setSpecies(null));
        speciesRepository.delete(speciesValue);
        return "Successfully deleted the species with id = " + id;
    }
}
