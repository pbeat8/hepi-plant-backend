package com.hepiplant.backend.controller;

import com.hepiplant.backend.dto.ScheduleDto;
import com.hepiplant.backend.service.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/schedules")
public class ScheduleController {
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    public ResponseEntity<ScheduleDto> addSchedule(@RequestBody ScheduleDto scheduleDto){
        return ResponseEntity.ok().body(scheduleService.add(scheduleDto));
    }

    @GetMapping
    public ResponseEntity<List<ScheduleDto>> getSchedules(){
        return ResponseEntity.ok().body(scheduleService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDto> getScheduleById(@PathVariable Long id){
        return  ResponseEntity.ok().body(scheduleService.getById(id));
    }

    @GetMapping("/plant/{plantId}")
    public ResponseEntity<List<ScheduleDto>> getSchedulesByPlant(@PathVariable Long plantId){
        return ResponseEntity.ok().body(scheduleService.getAllByPlant(plantId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ScheduleDto> update(@PathVariable Long id, @RequestBody ScheduleDto scheduleDto){
        return ResponseEntity.ok().body(scheduleService.update(id, scheduleDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        return ResponseEntity.ok().body(scheduleService.delete(id));
    }
}
