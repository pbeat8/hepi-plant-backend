package com.hepiplant.backend.service;


import com.hepiplant.backend.entity.Plant;
import org.springframework.stereotype.Service;

import java.util.List;

public interface PlantService {
    List<Plant> getAll();
    Plant getById(Long id);
}
