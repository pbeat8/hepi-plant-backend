package com.hepiplant.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Calendar;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long> {
}
