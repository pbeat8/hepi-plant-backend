package com.hepiplant.backend.service;


import com.hepiplant.backend.dto.PlantDto;
import com.hepiplant.backend.dto.SpeciesDto;

import java.util.List;
import java.util.Set;

public interface PlantService {
    PlantDto create(PlantDto plantDto);
    List<PlantDto> getAll();
    List<PlantDto> getAllByUser(Long userId);
    List<PlantDto> getAllByUserFiltered(Long userId, String name, Long speciesId, String location);
    PlantDto getById(Long id);
    PlantDto update(Long id, PlantDto plantDto);
    String delete(Long id);
    Set<String> getAllLocationsByUser(Long userId);

}
