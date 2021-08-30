package com.hepiplant.backend.service;

import com.hepiplant.backend.dto.SpeciesDto;
import com.hepiplant.backend.entity.Species;
import java.util.List;

public interface SpeciesService {
    List<SpeciesDto> getAll();
    SpeciesDto getById(Long id);
    SpeciesDto add(SpeciesDto speciesDto);
    SpeciesDto update(Long id, SpeciesDto speciesDto);
    String delete(Long id);
}
