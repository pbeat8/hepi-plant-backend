package com.hepiplant.backend.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(schema = "plants", name = "plants")
public class Plant {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(nullable = false)
    private String name;
    private LocalDateTime purchaseDate;
    private String location;
    @ManyToOne
    private Category category;
    @ManyToOne
    private Species species;
    @ManyToOne
    private User user;

    public Plant() {
    }

    public Plant(Long id, String name, LocalDateTime purchaseDate, String location, Category category, Species species, User user) {
        this.id = id;
        this.name = name;
        this.purchaseDate = purchaseDate;
        this.location = location;
        this.category = category;
        this.species = species;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Species getSpecies() {
        return species;
    }

    public void setSpecies(Species species) {
        this.species = species;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
