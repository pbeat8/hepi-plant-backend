package com.hepiplant.backend.controller;

import com.hepiplant.backend.dto.PlantDto;
import com.hepiplant.backend.entity.Plant;
import com.hepiplant.backend.service.PlantServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/plants")
public class PlantController {

    private PlantServiceImpl plantService;

    public PlantController(PlantServiceImpl plantService) {
        this.plantService = plantService;
    }

    @PostMapping
    public ResponseEntity<Plant> someMethod(@RequestBody PlantDto plantDto){
        return ResponseEntity.ok().body(plantService.savePlant(plantDto));
    }
    @GetMapping
    public ResponseEntity<?> getPlants(){
        return ResponseEntity.ok().body(plantService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPlantById(@PathVariable Long id){
        return  ResponseEntity.ok().body(plantService.getById(id));
    }

}
