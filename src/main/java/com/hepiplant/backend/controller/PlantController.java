package com.hepiplant.backend.controller;

import com.hepiplant.backend.dto.PlantDto;
import com.hepiplant.backend.service.PlantService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/plants")
public class PlantController {

    private final PlantService plantService;

    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<PlantDto> addPlant(@RequestBody PlantDto plantDto){
        return ResponseEntity.ok().body(plantService.create(plantDto));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<PlantDto>> getPlants(){
        return ResponseEntity.ok().body(plantService.getAll());
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PlantDto>> getPlantsByUser(@PathVariable Long userId){
        return ResponseEntity.ok().body(plantService.getAllByUser(userId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<PlantDto> getPlantById(@PathVariable Long id){
        return  ResponseEntity.ok().body(plantService.getById(id));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<PlantDto> update(@PathVariable Long id, @RequestBody PlantDto plantDto){
        return  ResponseEntity.ok().body(plantService.update(id, plantDto));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        return  ResponseEntity.ok().body(plantService.delete(id));
    }
}
