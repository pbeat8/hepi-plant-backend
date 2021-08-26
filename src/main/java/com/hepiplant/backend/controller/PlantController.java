package com.hepiplant.backend.controller;

import com.hepiplant.backend.dto.PlantDto;
import com.hepiplant.backend.entity.Plant;
import com.hepiplant.backend.service.PlantService;
import com.hepiplant.backend.service.impl.PlantServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/plants")
public class PlantController {

    private final PlantService plantService;

    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    // GET, POST - dodawanie, PATCH - updateowanie, DELETE

    @PostMapping
    public ResponseEntity<PlantDto> addPlant(@RequestBody PlantDto plantDto){
        return ResponseEntity.ok().body(plantService.savePlant(plantDto));
    }

    @GetMapping
    public ResponseEntity<List<PlantDto>> getPlants(){
        return ResponseEntity.ok().body(plantService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantDto> getPlantById(@PathVariable Long id){
        return  ResponseEntity.ok().body(plantService.getById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PlantDto> update(@PathVariable Long id, @RequestBody PlantDto plantDto){
        return  ResponseEntity.ok().body(plantService.update(id, plantDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        return  ResponseEntity.ok().body(plantService.delete(id));
    }

}
