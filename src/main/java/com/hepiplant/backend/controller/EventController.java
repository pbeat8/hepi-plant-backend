package com.hepiplant.backend.controller;

import com.hepiplant.backend.dto.CategoryDto;
import com.hepiplant.backend.dto.EventDto;
import com.hepiplant.backend.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }
    @PostMapping
    public ResponseEntity<EventDto> addEvent (@RequestBody EventDto eventDto) {
        return ResponseEntity.ok().body(eventService.add(eventDto));
    }
    @GetMapping
    public ResponseEntity<?> getEvents(){
        return ResponseEntity.ok().body(eventService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id){
        return  ResponseEntity.ok().body(eventService.getById(id));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<EventDto> update(@PathVariable Long id, @RequestBody EventDto eventDto){
        return ResponseEntity.ok().body(eventService.update(id, eventDto));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete (@PathVariable Long id){
        return ResponseEntity.ok().body(eventService.delete(id));
    }
}
