package com.hepiplant.backend.service;

import com.hepiplant.backend.entity.Event;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EventService {
    List<Event> getAll();
    Event getById(Long id);
}
