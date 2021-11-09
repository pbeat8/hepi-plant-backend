package com.hepiplant.backend.dto;

import java.util.Set;

public class TagDto {

    private Long id;
    private String name;
    private Set<Long> posts;
    private Set<Long> salesOffers;

    public TagDto() {
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

    public Set<Long> getPosts() {
        return posts;
    }

    public void setPosts(Set<Long> posts) {
        this.posts = posts;
    }

    public Set<Long> getSalesOffers() {
        return salesOffers;
    }

    public void setSalesOffers(Set<Long> salesOffers) {
        this.salesOffers = salesOffers;
    }
}
