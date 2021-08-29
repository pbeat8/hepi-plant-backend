package com.hepiplant.backend.service.impl;


import com.hepiplant.backend.dto.EventDto;
import com.hepiplant.backend.entity.Category;
import com.hepiplant.backend.entity.Event;
import com.hepiplant.backend.entity.Plant;
import com.hepiplant.backend.entity.Species;
import com.hepiplant.backend.repository.EventRepository;
import com.hepiplant.backend.repository.PlantRepository;
import com.hepiplant.backend.service.EventService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {
    public EventRepository eventRepository;
    public PlantRepository plantRepository;
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

    @Override
    public EventDto add(EventDto eventDto) {
        Event event = new Event();
        if(eventDto.getEventName()!=null && !eventDto.getEventName().isEmpty())
            event.setEventName(eventDto.getEventName());
        if(eventDto.getEventDescription()!=null && !eventDto.getEventName().isEmpty())
            event.setEventDescription(eventDto.getEventDescription());
        event.setEventDate(eventDto.getEventDate());
        event.setDone(eventDto.isDone());
        if(eventDto.getPlantId()!=null) {
            Plant plant = plantRepository.findById(eventDto.getPlantId()).orElseThrow(EntityNotFoundException::new);
            event.setPlant(plant);
        }
        Event savedEvent = eventRepository.save(event);
        return mapToDto(savedEvent);
    }

    @Override
    public EventDto update(Long id, EventDto eventDto) {
        Event event = eventRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if(eventDto.getEventName()!=null && !eventDto.getEventName().isEmpty())
            event.setEventName(eventDto.getEventName());
        if(eventDto.getEventDescription()!=null && !eventDto.getEventName().isEmpty())
            event.setEventDescription(eventDto.getEventDescription());
        event.setEventDate(eventDto.getEventDate());
        event.setDone(eventDto.isDone());
        if(eventDto.getPlantId()!=null) {
            Plant plant = plantRepository.findById(eventDto.getPlantId()).orElseThrow(EntityNotFoundException::new);
            event.setPlant(plant);
        }
        Event savedEvent = eventRepository.save(event);
        return mapToDto(savedEvent);
    }

    @Override
    public String delete(Long id) {
        Optional<Event> event = eventRepository.findById(id);
        if(event.isEmpty()){
            return "No event with id = " + id;
        }
        eventRepository.delete(event.get());
        return "Successfully deleted the event with id = "+ id;
    }
    private EventDto mapToDto(Event event) {
        EventDto dto = new EventDto();
        if (event.getEventName()!=null)
            dto.setEventName(event.getEventName());
        if (event.getEventDescription()!=null)
            dto.setEventDescription(event.getEventDescription());
        dto.setEventDate(event.getEventDate());
        dto.setDone(event.isDone());
        if (event.getPlant()!=null)
            dto.setPlantId(event.getPlant().getId());
        return dto;
    }
}
