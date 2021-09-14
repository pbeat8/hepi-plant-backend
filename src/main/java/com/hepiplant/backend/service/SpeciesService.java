package com.hepiplant.backend.service;

import com.hepiplant.backend.dto.SpeciesDto;

import java.util.List;

public interface SpeciesService {
    List<SpeciesDto> getAll();
    SpeciesDto getById(Long id);
    SpeciesDto add(SpeciesDto speciesDto);
    SpeciesDto update(Long id, SpeciesDto speciesDto);
    String delete(Long id);
}
