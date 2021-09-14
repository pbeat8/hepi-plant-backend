package com.hepiplant.backend.controller;

import com.hepiplant.backend.dto.SpeciesDto;
import com.hepiplant.backend.service.SpeciesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/species")
public class SpeciesController {

    private final SpeciesService speciesService;

    public SpeciesController(SpeciesService speciesService) {
        this.speciesService = speciesService;
    }

    @PostMapping
    public ResponseEntity<SpeciesDto> addSpecies(@RequestBody SpeciesDto speciesDto){
        return ResponseEntity.ok().body(speciesService.add(speciesDto));
    }
    @GetMapping
    public ResponseEntity<List<SpeciesDto>> getSpecies(){
        return ResponseEntity.ok().body(speciesService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpeciesDto> getSpeciesById(@PathVariable Long id){
        return  ResponseEntity.ok().body(speciesService.getById(id));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<SpeciesDto> update(@PathVariable Long id, @RequestBody SpeciesDto speciesDto){
        return  ResponseEntity.ok().body(speciesService.update(id, speciesDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        return  ResponseEntity.ok().body(speciesService.delete(id));
    }

}
