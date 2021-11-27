package com.hepiplant.backend.service;


import com.hepiplant.backend.dto.PlantDto;
import com.hepiplant.backend.dto.SpeciesDto;

import java.util.List;

public interface PlantService {
    PlantDto create(PlantDto plantDto);
    List<PlantDto> getAll();
    List<PlantDto> getAllByUser(Long userId);
    List<PlantDto> getAllByUserFiltered(Long userId, String name, Long speciesId, String location);
    PlantDto getById(Long id);
    PlantDto update(Long id, PlantDto plantDto);
    String delete(Long id);
    List<String> getAllLocationsByUser(Long userId);

}
