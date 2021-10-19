package com.hepiplant.backend.controller;

import com.hepiplant.backend.dto.SpeciesDto;
import com.hepiplant.backend.service.SpeciesService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/species")
public class SpeciesController {

    private final SpeciesService speciesService;

    public SpeciesController(SpeciesService speciesService) {
        this.speciesService = speciesService;
    }

    @PreAuthorize("hasRole( 'ADMIN')")
    @PostMapping
    public ResponseEntity<SpeciesDto> addSpecies(@RequestBody SpeciesDto speciesDto){
        return ResponseEntity.ok().body(speciesService.add(speciesDto));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<SpeciesDto>> getSpecies(){
        return ResponseEntity.ok().body(speciesService.getAll());
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<SpeciesDto> getSpeciesById(@PathVariable Long id){
        return  ResponseEntity.ok().body(speciesService.getById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<SpeciesDto> update(@PathVariable Long id, @RequestBody SpeciesDto speciesDto){
        return  ResponseEntity.ok().body(speciesService.update(id, speciesDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        return  ResponseEntity.ok().body(speciesService.delete(id));
    }
}
