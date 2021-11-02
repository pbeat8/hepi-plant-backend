package com.hepiplant.backend.repository;

import com.hepiplant.backend.entity.Event;
import com.hepiplant.backend.entity.Plant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByPlant(Plant plant);
//    List<Event> findAllByUser(Long id);
}
