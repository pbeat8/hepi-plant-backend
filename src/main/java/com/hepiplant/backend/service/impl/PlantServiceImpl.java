package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.PlantDto;
import com.hepiplant.backend.entity.Plant;
import com.hepiplant.backend.entity.Species;
import com.hepiplant.backend.exception.ImmutableFieldException;
import com.hepiplant.backend.repository.PlantRepository;
import com.hepiplant.backend.repository.SpeciesRepository;
import com.hepiplant.backend.service.PlantService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlantServiceImpl implements PlantService {

    private PlantRepository plantRepository;
    private SpeciesRepository speciesRepository;

    public PlantServiceImpl(PlantRepository plantRepository, SpeciesRepository speciesRepository) {
        this.plantRepository = plantRepository;
        this.speciesRepository = speciesRepository;
    }

    public PlantDto create(PlantDto plantDto){
        // todo check if fields have acceptable values
        Plant plant = new Plant();
        plant.setName(plantDto.getName());
        plant.setPurchaseDate(LocalDateTime.now());
        plant.setLocation(plantDto.getLocation());
        if(plantDto.getSpeciesId()!=null){
            Species species = speciesRepository.findById(plantDto.getSpeciesId()).orElseThrow(EntityNotFoundException::new);
            plant.setSpecies(species);
            plant.setCategory(species.getCategory());
        }
        Plant savedPlant = plantRepository.save(plant);
        return mapToDto(savedPlant);
    }

    @Override
    public PlantDto update(Long id, PlantDto plantDto) {
        Plant plant = plantRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if(plantDto.getName()!=null && !plantDto.getName().isEmpty()){
            plant.setName(plantDto.getName());
        }
        if(plantDto.getLocation()!=null && !plantDto.getLocation().isEmpty()){
            plant.setLocation(plantDto.getLocation());
        }
        if(plantDto.getCategoryId()!=null && !plantDto.getCategoryId().equals(plant.getCategory().getId())){
            throw new ImmutableFieldException("Field Category in Plant is immutable!");
        }
        if(plantDto.getSpeciesId()!=null && (plant.getSpecies()==null || (plant.getSpecies().getId()!=null && !plantDto.getSpeciesId().equals(plant.getSpecies().getId())))){
            Species species = speciesRepository.findById(plantDto.getSpeciesId()).orElseThrow(EntityNotFoundException::new);
            plant.setSpecies(species);
            plant.setCategory(species.getCategory());
        }
        if(plantDto.getUserId()!=null && !plantDto.getUserId().equals(plant.getUser().getId())){
            throw new ImmutableFieldException("Field User in Plant is immutable!");
        }
        Plant savedPlant = plantRepository.save(plant);
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

    @Override
    public List<PlantDto> getAll() {
        return plantRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PlantDto getById(Long id) {
        Plant plant = plantRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return mapToDto(plant);
    }

    private PlantDto mapToDto(Plant plant){
        PlantDto dto = new PlantDto();
        dto.setName(plant.getName());
        dto.setLocation(plant.getLocation());
        if(plant.getCategory()!=null) {
            dto.setCategoryId(plant.getCategory().getId());
        }
        if(plant.getSpecies()!=null) {
            dto.setSpeciesId(plant.getSpecies().getId());
        }
        if(plant.getUser()!=null){
            dto.setUserId(plant.getUser().getId());
        }
        return dto;
    }
}
