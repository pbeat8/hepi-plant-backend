package com.hepiplant.backend.service;

import com.hepiplant.backend.dto.EventDto;
import com.hepiplant.backend.entity.Event;
import org.springframework.stereotype.Service;

import java.util.List;

public interface EventService {
    List<Event> getAll();
    Event getById(Long id);
    EventDto add(EventDto eventDto);
    EventDto update(Long id, EventDto eventDto);
    String delete(Long id);
}
