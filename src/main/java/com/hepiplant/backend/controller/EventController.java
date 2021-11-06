package com.hepiplant.backend.controller;

import com.hepiplant.backend.dto.EventDto;
import com.hepiplant.backend.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//todo add @PreAuthorize

@RestController
@RequestMapping(path = "/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<EventDto> addEvent (@RequestBody EventDto eventDto) {
        return ResponseEntity.ok().body(eventService.add(eventDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<EventDto>> getEvents(){
        return ResponseEntity.ok().body(eventService.getAll());
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getEventById(@PathVariable Long id){
        return  ResponseEntity.ok().body(eventService.getById(id));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/plant/{plantId}")
    public ResponseEntity<List<EventDto>> getEventsByPlant(@PathVariable Long plantId){
        return ResponseEntity.ok().body(eventService.getAllByPlant(plantId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EventDto>> getEventsByUser(@PathVariable Long userId){
        return ResponseEntity.ok().body(eventService.getAllByUser(userId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<EventDto> update(@PathVariable Long id, @RequestBody EventDto eventDto){
        return ResponseEntity.ok().body(eventService.update(id, eventDto));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete (@PathVariable Long id){
        return ResponseEntity.ok().body(eventService.delete(id));
    }
}
