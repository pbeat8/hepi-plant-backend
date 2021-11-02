package com.hepiplant.backend.service.impl;


import com.hepiplant.backend.dto.EventDto;
import com.hepiplant.backend.entity.Event;
import com.hepiplant.backend.entity.Plant;
import com.hepiplant.backend.entity.User;
import com.hepiplant.backend.exception.ImmutableFieldException;
import com.hepiplant.backend.mapper.DtoMapper;
import com.hepiplant.backend.repository.EventRepository;
import com.hepiplant.backend.repository.PlantRepository;
import com.hepiplant.backend.repository.UserRepository;
import com.hepiplant.backend.service.EventService;
import com.hepiplant.backend.validator.BeanValidator;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hepiplant.backend.mapper.DtoMapper.mapToDto;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final PlantRepository plantRepository;
    private final UserRepository userRepository;
    private final BeanValidator beanValidator;

    public EventServiceImpl(final EventRepository eventRepository,
                            final PlantRepository plantRepository,
                            final UserRepository userRepository, final BeanValidator beanValidator) {
        this.eventRepository = eventRepository;
        this.plantRepository = plantRepository;
        this.userRepository = userRepository;
        this.beanValidator = beanValidator;
    }

    @Override
    public List<EventDto> getAll() {
        return eventRepository.findAll().stream()
                .map(DtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDto> getAllByPlant(Long plantId) {
        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new EntityNotFoundException("Plant not found for id "+plantId));
        return eventRepository.findAllByPlant(plant).stream()
                .filter(e -> e.isDone())
                .map(DtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDto> getAllByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found for id "+userId));
        return eventRepository.findAll().stream()
                .filter(e -> e.getPlant().getUser().getId().equals(userId))
                .filter(e -> !e.isDone())
                .map(DtoMapper :: mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventDto getById(Long id) {
        return mapToDto(eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found for id "+id)));
    }

    @Override
    public EventDto add(EventDto eventDto) {
        Event event = new Event();
        event.setEventName(eventDto.getEventName());
        event.setEventDescription(eventDto.getEventDescription());
        event.setEventDate(eventDto.getEventDate());
        event.setDone(eventDto.isDone());
        if(eventDto.getPlantId()!=null) {
            Plant plant = plantRepository.findById(eventDto.getPlantId()).orElseThrow(EntityNotFoundException::new);
            event.setPlant(plant);
        }
        beanValidator.validate(event);
        Event savedEvent = eventRepository.save(event);
        return mapToDto(savedEvent);
    }

    @Override
    public EventDto update(Long id, EventDto eventDto) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found for id " + id));
        if(eventDto.getEventName()!=null && !eventDto.getEventName().isEmpty())
            event.setEventName(eventDto.getEventName());
        if(eventDto.getEventDescription()!=null && !eventDto.getEventDescription().isEmpty())
            event.setEventDescription(eventDto.getEventDescription());
        if(eventDto.getEventDate()!=null)
            event.setEventDate(eventDto.getEventDate());
        event.setDone(eventDto.isDone());
        if(eventDto.getPlantId()!=null) {
            throw new ImmutableFieldException("Field Plant in Event is immutable!");
        }
        beanValidator.validate(event);
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
}
