package com.hepiplant.backend.service;

import com.hepiplant.backend.dto.PlantDto;
import com.hepiplant.backend.entity.Plant;
import com.hepiplant.backend.repository.PlantRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PlantService {

    private PlantRepository plantRepository;

    public PlantService(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }

    public Plant savePlant(PlantDto plantDto){
        Plant plant = new Plant();
        plant.setName(plantDto.getName());
        plant.setPurchaseDate(LocalDateTime.now());
        plant.setLocation(plantDto.getLocation());
        return plantRepository.save(plant);
    }

}
