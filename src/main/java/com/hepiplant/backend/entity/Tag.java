package com.hepiplant.backend.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(schema = "forum", name = "tags")
@SequenceGenerator(name = "forum.tags_seq", allocationSize = 1)
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "forum.tags_seq")
    private Long id;
    @NotNull
    @Size(min=1, max=255)
    private String name;

    @ManyToMany
    @JoinTable(schema = "forum", name = "post_tag",
            joinColumns = @JoinColumn(name = "tag_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id"))
    private Set<Post> posts;

    @ManyToMany
    @JoinTable(schema = "forum", name = "sales_offer_tag",
            joinColumns = @JoinColumn(name = "tag_id"),
            inverseJoinColumns = @JoinColumn(name = "sales_offer_id"))
    private Set<SalesOffer> salesOffers;


    public Tag() {
    }

    public Tag(Long id, String name, Set<Post> posts, Set<SalesOffer> salesOffers) {
        this.id = id;
        this.name = name;
        this.posts = posts;
        this.salesOffers = salesOffers;
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

    public Set<Post> getPosts() {
        return posts;
    }

    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }

    public Set<SalesOffer> getSalesOffers() {
        return salesOffers;
    }

    public void setSalesOffers(Set<SalesOffer> salesOffers) {
        this.salesOffers = salesOffers;
    }
}
