package com.hepiplant.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(schema = "forum", name = "sales_offers")
@SequenceGenerator(name = "forum.sales_offers_seq", allocationSize = 1)
public class SalesOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "forum.sales_offers_seq")
    private Long id;
    @NotBlank
    @Size(min=1, max=255)
    private String title;
    @NotBlank
    @Size(min=1, max=255) // todo we will need to make it bigger later
    private String body;
    @NotBlank
    @Size(min=1, max=255)
    private String location;
    @PositiveOrZero
    private BigDecimal price;

    private String photo;
    @CreationTimestamp
    private LocalDateTime createdDate;
    @UpdateTimestamp
    private LocalDateTime updatedDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    @OneToMany(mappedBy = "salesOffer", orphanRemoval = true, cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<SalesOfferComment> commentList;
    @ManyToMany
    @JoinTable(schema = "forum", name = "sales_offer_tag",
            joinColumns = @JoinColumn(name = "sales_offer_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags;

    public SalesOffer() {
    }

    public SalesOffer(Long id, String title, String body, String location, BigDecimal price, String photo, User user, Category category, Set<Tag> tags) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.location = location;
        this.price = price;
        this.photo = photo;
        this.user = user;
        this.category = category;
        this.tags = tags;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<SalesOfferComment> getComments() {
        return commentList;
    }

    public void setComments(List<SalesOfferComment> commentList) {
        this.commentList = commentList;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
}
