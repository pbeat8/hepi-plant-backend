package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.PlantDto;
import com.hepiplant.backend.entity.Plant;
import com.hepiplant.backend.repository.PlantRepository;
import com.hepiplant.backend.service.PlantService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlantServiceImpl implements PlantService {

    private PlantRepository plantRepository;

    public PlantServiceImpl(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }

    public Plant savePlant(PlantDto plantDto){
        Plant plant = new Plant();
        plant.setName(plantDto.getName());
        plant.setPurchaseDate(LocalDateTime.now());
        plant.setLocation(plantDto.getLocation());
        return plantRepository.save(plant);
    }

    @Override
    public List<Plant> getAll() {
        return plantRepository.findAll();
    }

    @Override
    public Plant getById(Long id) {
        return plantRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
    }
}
