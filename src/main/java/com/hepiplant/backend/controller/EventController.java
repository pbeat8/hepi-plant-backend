package com.hepiplant.backend.controller;

import com.hepiplant.backend.dto.EventDto;
import com.hepiplant.backend.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<EventDto>> getEvents(){
        return ResponseEntity.ok().body(eventService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getEventById(@PathVariable Long id){
        return  ResponseEntity.ok().body(eventService.getById(id));
    }
    @GetMapping("/plant/{plandId}")
    public ResponseEntity<List<EventDto>> getEventsByPlant(@PathVariable Long plantId){
        return ResponseEntity.ok().body(eventService.getAllByPlant(plantId));
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
