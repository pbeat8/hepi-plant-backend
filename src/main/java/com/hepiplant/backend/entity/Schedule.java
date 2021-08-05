package com.hepiplant.backend.entity;

import javax.persistence.*;

@Entity
@Table(schema = "plants", name = "schedules")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @OneToOne
    private Plant plant;
    private int wateringFrequency;
    private int fertilizingFrequency;
    private int mistingFrequency;

    public Schedule(Plant plant, int wateringFrequency, int fertilizingFrequency, int mistingFrequency) {
        this.plant = plant;
        this.wateringFrequency = wateringFrequency;
        this.fertilizingFrequency = fertilizingFrequency;
        this.mistingFrequency = mistingFrequency;
    }

    public Schedule() {
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
