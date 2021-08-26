package com.hepiplant.backend.controller;

import com.hepiplant.backend.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<?> getEvents(){
        return ResponseEntity.ok().body(eventService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id){
        return  ResponseEntity.ok().body(eventService.getById(id));
    }
}
