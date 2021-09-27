package com.hepiplant.backend.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(schema = "plants", name = "events")
@SequenceGenerator(name = "plants.events_seq", allocationSize = 1)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plants.events_seq")
    private Long id;
    @NotBlank
    @Size(min=1, max=255)
    private String eventName;
    @Size(min=1, max=255)
    private String eventDescription;
    @PastOrPresent
    private LocalDateTime eventDate;
    @NotBlank
    private boolean isDone;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id")
    private Plant plant;

    public Event() {
    }

    public Event(Long id, String eventName, String eventDescription, LocalDateTime eventDate, boolean isDone, Plant plant) {
        this.id = id;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.isDone = isDone;
        this.plant = plant;
    }

    public Plant getPlant() {
        return plant;
    }

    public Long getId() {
        return id;
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

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
