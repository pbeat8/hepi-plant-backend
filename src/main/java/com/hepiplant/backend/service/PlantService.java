package com.hepiplant.backend.service;


import com.hepiplant.backend.dto.PlantDto;
import com.hepiplant.backend.entity.Plant;

import java.util.List;

public interface PlantService {
    List<PlantDto> getAll();
    PlantDto getById(Long id);
    PlantDto savePlant(PlantDto plantDto);
    PlantDto update(Long id, PlantDto plantDto);
    String delete(Long id);
}
