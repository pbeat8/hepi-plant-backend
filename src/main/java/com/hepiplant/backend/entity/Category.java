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
    private List<Species> speciesList;
    @OneToMany(mappedBy = "category")
    private List<Plant> plantList;
    @OneToMany(mappedBy = "category")
    private List<Post> postList;
    @OneToMany(mappedBy = "category")
    private List<SalesOffer> salesOfferList;

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

    public List<Plant> getPlantList() {
        return plantList;
    }

    public void setPlantList(List<Plant> plantList) {
        this.plantList = plantList;
    }

    public List<Post> getPostList() {
        return postList;
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
    }

    public List<SalesOffer> getSalesOfferList() {
        return salesOfferList;
    }

    public void setSalesOfferList(List<SalesOffer> salesOfferList) {
        this.salesOfferList = salesOfferList;
    }
}
