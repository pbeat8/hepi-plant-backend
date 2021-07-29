package com.hepiplant.backend.entity;

import javax.persistence.*;

@Entity
@Table(schema = "plants", name = "species")
public class Species {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    private int wateringFrequency;
    private int fertilizingFrequency;
    private int mistingFrequency;
    private String placement;
    private String soil;

    public Species() {
    }

    public Species(Long id, String name, int wateringFrequency, int fertilizingFrequency, int mistingFrequency, String placement, String soil) {
        this.id = id;
        this.name = name;
        this.wateringFrequency = wateringFrequency;
        this.fertilizingFrequency = fertilizingFrequency;
        this.mistingFrequency = mistingFrequency;
        this.placement = placement;
        this.soil = soil;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPlacement() {
        return placement;
    }

    public void setPlacement(String placement) {
        this.placement = placement;
    }

    public String getSoil() {
        return soil;
    }

    public void setSoil(String soil) {
        this.soil = soil;
    }
}
