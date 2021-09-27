package com.hepiplant.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(schema = "plants", name = "categories")
@SequenceGenerator(name = "plants.categories_seq", allocationSize = 1)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plants.categories_seq")
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<Species> speciesList;

    public Category() {
    }

    public Category(Long id, String name, List<Species> speciesList) {
        this.id = id;
        this.name = name;
        this.speciesList = speciesList;
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

    public List<Species> getSpeciesList() {
        return speciesList;
    }

    public void setSpeciesList(List<Species> speciesList) {
        this.speciesList = speciesList;
    }
}
