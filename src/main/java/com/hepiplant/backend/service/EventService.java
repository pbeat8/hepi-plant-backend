package com.hepiplant.backend.service;

import com.hepiplant.backend.dto.EventDto;

import java.util.List;

public interface EventService {
    List<EventDto> getAll();
    List<EventDto> getAllByPlant(Long plantId);
    EventDto getById(Long id);
    EventDto add(EventDto eventDto);
    EventDto update(Long id, EventDto eventDto);
    String delete(Long id);
}
