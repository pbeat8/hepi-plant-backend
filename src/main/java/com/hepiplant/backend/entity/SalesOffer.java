package com.hepiplant.backend.entity;

import javax.persistence.*;

@Entity
@Table(schema = "forum", name = "salesoffers")
public class SalesOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String offerTitle;
    private String offerContent;
    private String location;
    private Double price;
    private String tag1;
    private String tag2;
    private String tag3;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    public SalesOffer(Long id, Category category, String offerTitle, String offerContent, String location, Double price, String tag1, String tag2, String tag3) {
        this.id = id;
        this.category = category;
        this.offerTitle = offerTitle;
        this.offerContent = offerContent;
        this.location = location;
        this.price = price;
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.tag3 = tag3;
    }

    public SalesOffer() {
    }

    public Long getId() {
        return id;
    }

    public Category getCategory() {
        return category;
    }

    public String getOfferTitle() {
        return offerTitle;
    }

    public String getOfferContent() {
        return offerContent;
    }

    public String getLocation() {
        return location;
    }

    public Double getPrice() {
        return price;
    }

    public String getTag1() {
        return tag1;
    }

    public String getTag2() {
        return tag2;
    }

    public String getTag3() {
        return tag3;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setOfferTitle(String offerTitle) {
        this.offerTitle = offerTitle;
    }

    public void setOfferContent(String offerContent) {
        this.offerContent = offerContent;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setTag1(String tag1) {
        this.tag1 = tag1;
    }

    public void setTag2(String tag2) {
        this.tag2 = tag2;
    }

    public void setTag3(String tag3) {
        this.tag3 = tag3;
    }
}
