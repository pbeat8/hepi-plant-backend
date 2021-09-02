package com.hepiplant.backend.service;


import com.hepiplant.backend.dto.PlantDto;

import java.util.List;

public interface PlantService {
    PlantDto create(PlantDto plantDto);
    List<PlantDto> getAll();
    List<PlantDto> getAllByUser(Long userId);
    PlantDto getById(Long id);
    PlantDto update(Long id, PlantDto plantDto);
    String delete(Long id);
}
