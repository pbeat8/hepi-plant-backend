package com.hepiplant.backend.service;


import com.hepiplant.backend.entity.Event;
import com.hepiplant.backend.repository.EventRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class EventServiceImpl implements EventService{
    public EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<Event> getAll() {
        return eventRepository.findAll();
    }

    @Override
    public Event getById(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
    }
}
