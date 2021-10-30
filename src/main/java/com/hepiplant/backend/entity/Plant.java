package com.hepiplant.backend.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(schema = "plants", name = "plants")
@SequenceGenerator(name = "plants.plants_seq", allocationSize = 1)
public class Plant {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plants.plants_seq")
    private Long id;
    @NotBlank
    @Size(min=1, max=255)
    private String name;
    @PastOrPresent
    private LocalDateTime purchaseDate;
    @Size(min=1, max=255)
    private String location;
    private String photo;
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="species_id")
    private Species species;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    @NotNull
    private User user;
    @OneToMany(mappedBy = "plant", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private List<Event> eventList;
    @OneToOne(mappedBy = "plant", cascade = CascadeType.REMOVE)
    private Schedule schedule;

    public Plant() {
    }

    public Plant(Long id, String name, LocalDateTime purchaseDate, String location, String photo, Category category, Species species, User user, List<Event> eventList, Schedule schedule) {
        this.id = id;
        this.name = name;
        this.purchaseDate = purchaseDate;
        this.location = location;
        this.photo = photo;
        this.category = category;
        this.species = species;
        this.user = user;
        this.eventList = eventList;
        this.schedule = schedule;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
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

    public List<Event> getEventList() {
        return eventList;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
}
