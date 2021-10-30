package com.hepiplant.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hepiplant.backend.entity.enums.Placement;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(schema = "plants", name = "species")
@SequenceGenerator(name = "plants.species_seq", allocationSize = 1)
public class Species {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plants.species_seq")
    private Long id;
    @Column(nullable = false, unique = true)
    @NotNull
    @Size(min=1, max=255)
    private String name;
    @Min(value = 1, message = "Watering frequency for species should be greater than 0")
    private int wateringFrequency;
    @Min(value = 0, message = "Fertilizing frequency for species should be greater or equals 0")
    private int fertilizingFrequency;
    @Min(value = 0, message = "Misting frequency for species should be greater or equals 0")
    private int mistingFrequency;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Placement placement;
    @NotNull
    @Size(min=1, max=255)
    private String soil;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Category category;
    @OneToMany(mappedBy = "species")
    private List<Plant> plantList;

    public Species() {
    }

    public Species(Long id, String name, int wateringFrequency, int fertilizingFrequency,
                   int mistingFrequency, Placement placement, String soil, Category category) {
        this.id = id;
        this.name = name;
        this.wateringFrequency = wateringFrequency;
        this.fertilizingFrequency = fertilizingFrequency;
        this.mistingFrequency = mistingFrequency;
        this.placement = placement;
        this.soil = soil;
        this.category = category;
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

    public Placement getPlacement() {
        return placement;
    }

    public void setPlacement(Placement placement) {
        this.placement = placement;
    }

    public String getSoil() {
        return soil;
    }

    public void setSoil(String soil) {
        this.soil = soil;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Plant> getPlantList() {
        return plantList;
    }

    public void setPlantList(List<Plant> plantList) {
        this.plantList = plantList;
    }
}
