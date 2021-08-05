package com.hepiplant.backend.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(schema = "plants", name = "calendars")
public class Calendar {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @ManyToOne
    private Plant plant;
    private String eventName;
    private String eventDescription;
    private LocalDateTime eventDate;

    public Calendar() {
    }

    public Calendar(Plant plant, String eventName, String eventDescription, LocalDateTime eventDate) {
        this.plant = plant;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }
}
