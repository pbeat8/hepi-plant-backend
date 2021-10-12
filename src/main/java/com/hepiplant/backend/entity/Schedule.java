package com.hepiplant.backend.entity;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(schema = "plants", name = "schedules")
@SequenceGenerator(name = "plants.schedules_seq", allocationSize = 1)
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plants.schedules_seq")
    @Column(name = "id")
    private Long id;
    @NotNull
    @Min(value = 0, message = "Watering frequency should be greater or equals 0")
    private int wateringFrequency;
    @Min(value = 0, message = "Fertilizing frequency should be greater or equals  0")
    private int fertilizingFrequency;
    @Min(value = 0, message = "Misting frequency should be greater or equals 0")
    private int mistingFrequency;
    @NotNull
    @OneToOne
    @JoinColumn(name = "plant_id", referencedColumnName = "id")
    private Plant plant;

    public Schedule(Long id, Plant plant, int wateringFrequency, int fertilizingFrequency, int mistingFrequency) {
        this.id = id;
        this.plant = plant;
        this.wateringFrequency = wateringFrequency;
        this.fertilizingFrequency = fertilizingFrequency;
        this.mistingFrequency = mistingFrequency;
    }

    public Schedule() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public int getWateringFrequency() {
        return wateringFrequency;
    }

    public void setWateringFrequency(int wateringFrequency) {
        this.wateringFrequency = wateringFrequency;
    }

    public int getFertilizingFrequency() {
        return fertilizingFrequency;
    }

    public void setFertilizingFrequency(int fertilizingFrequency) {
        this.fertilizingFrequency = fertilizingFrequency;
    }

    public int getMistingFrequency() {
        return mistingFrequency;
    }

    public void setMistingFrequency(int mistingFrequency) {
        this.mistingFrequency = mistingFrequency;
    }
}
