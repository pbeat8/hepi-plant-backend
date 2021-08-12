package com.hepiplant.backend.controller;

import com.hepiplant.backend.entity.Schedule;
import com.hepiplant.backend.service.CategoryService;
import com.hepiplant.backend.service.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/schedules")
public class ScheduleController {
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public ResponseEntity<?> getSchedules(){
        return ResponseEntity.ok().body(scheduleService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getScheduleById(@PathVariable Long id){
        return  ResponseEntity.ok().body(scheduleService.getById(id));
    }
}
