package com.hepiplant.backend.controller;

import com.hepiplant.backend.service.ScheduleService;
import com.hepiplant.backend.service.SpeciesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/species")
public class SpeciesController {

    private final SpeciesService speciesService;

    public SpeciesController(SpeciesService speciesService) {
        this.speciesService = speciesService;
    }

    @GetMapping
    public ResponseEntity<?> getSpecies(){
        return ResponseEntity.ok().body(speciesService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSpeciesById(@PathVariable Long id){
        return  ResponseEntity.ok().body(speciesService.getById(id));
    }
}
