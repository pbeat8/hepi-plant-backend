package com.hepiplant.backend.service;

import com.hepiplant.backend.entity.Species;
import com.hepiplant.backend.repository.SpeciesRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class SpeciesServiceImpl implements SpeciesService{
    public SpeciesRepository speciesRepository;

    public SpeciesServiceImpl(SpeciesRepository speciesRepository) {
        this.speciesRepository = speciesRepository;
    }

    @Override
    public List<Species> getAll() {
        return speciesRepository.findAll();
    }

    @Override
    public Species getById(Long id) {
        return speciesRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
    }
}
