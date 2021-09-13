package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.SpeciesDto;
import com.hepiplant.backend.entity.Category;
import com.hepiplant.backend.entity.Species;
import com.hepiplant.backend.exception.ImmutableFieldException;
import com.hepiplant.backend.repository.CategoryRepository;
import com.hepiplant.backend.repository.SpeciesRepository;
import com.hepiplant.backend.service.SpeciesService;
import com.hepiplant.backend.validator.BeanValidator;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SpeciesServiceImpl implements SpeciesService {

    private final SpeciesRepository speciesRepository;
    private final CategoryRepository categoryRepository;
    private final BeanValidator beanValidator;

    public SpeciesServiceImpl(SpeciesRepository speciesRepository, CategoryRepository categoryRepository, BeanValidator beanValidator) {

        this.speciesRepository = speciesRepository;
        this.categoryRepository = categoryRepository;
        this.beanValidator = beanValidator;
    }

    @Override
    public List<SpeciesDto> getAll() {
        return speciesRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
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
        if(speciesDto.getWateringFrequency()>=0)
            species.setWateringFrequency(speciesDto.getWateringFrequency());
        if(speciesDto.getFertilizingFrequency()>=0)
            species.setFertilizingFrequency(speciesDto.getFertilizingFrequency());
        if(speciesDto.getMistingFrequency()>=0)
            species.setMistingFrequency(speciesDto.getMistingFrequency());
        if(speciesDto.getPlacement()!=null )
            species.setPlacement(speciesDto.getPlacement());
        if(speciesDto.getSoil()!=null && !speciesDto.getSoil().isEmpty())
            species.setSoil(speciesDto.getSoil());

        Category category = categoryRepository.findById(speciesDto.getCategoryId()).orElseThrow(() -> new EntityNotFoundException("Category not found for id "+speciesDto.getCategoryId()));
        species.setCategory(category);

        beanValidator.validate(species);
        Species savedSpecies = speciesRepository.save(species);
        return mapToDto(savedSpecies);
    }

    @Override
    public SpeciesDto update(Long id, SpeciesDto speciesDto) {
        Species species = speciesRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Species not found for id "+id));
        if(speciesDto.getName()!=null && !speciesDto.getName().isEmpty())
            species.setName(speciesDto.getName());
        if(speciesDto.getWateringFrequency()>=0)
            species.setWateringFrequency(speciesDto.getWateringFrequency());
        if(speciesDto.getFertilizingFrequency()>=0)
            species.setFertilizingFrequency(speciesDto.getFertilizingFrequency());
        if(speciesDto.getMistingFrequency()>=0)
            species.setMistingFrequency(speciesDto.getMistingFrequency());
        if(speciesDto.getPlacement()!=null )
            species.setPlacement(speciesDto.getPlacement());
        if(speciesDto.getSoil()!=null && !speciesDto.getSoil().isEmpty() && !speciesDto.getSoil().equals(species.getSoil())){
            throw new ImmutableFieldException("Field Soil in Species is immutable!");
        }
        if(speciesDto.getCategoryId()!=null &&  !speciesDto.getSoil().isEmpty() && !speciesDto.getCategoryId().equals(species.getCategory().getId())){
            throw new ImmutableFieldException("Field Soil in Species is immutable!");

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
        speciesRepository.delete(species.get());
        return "Successfully deleted the species with id = "+ id;
    }
    private SpeciesDto mapToDto(Species species){
        SpeciesDto dto = new SpeciesDto();

        if(species.getName()!=null){
            dto.setName(species.getName());
        }
        dto.setWateringFrequency(species.getWateringFrequency());
        dto.setFertilizingFrequency(species.getFertilizingFrequency());
        dto.setMistingFrequency(species.getMistingFrequency());
        if(species.getPlacement()!=null){
            dto.setPlacement(species.getPlacement());
        }
        if(species.getSoil()!=null){
            dto.setSoil(species.getSoil());
        }
        if(species.getCategory()!=null){
            dto.setCategoryId(species.getCategory().getId());
        }
        return dto;
    }
}
