package com.hepiplant.backend.service;

import com.hepiplant.backend.entity.Species;
import java.util.List;

public interface SpeciesService {
    List<Species> getAll();
    Species getById(Long id);
}
