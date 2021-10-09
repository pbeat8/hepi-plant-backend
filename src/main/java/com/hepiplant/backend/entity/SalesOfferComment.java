package com.hepiplant.backend.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(schema = "forum", name = "sales_offer_comments")
@SequenceGenerator(name = "forum.sales_offer_comments_seq", allocationSize = 1)
public class SalesOfferComment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "forum.sales_offer_comments_seq")
    private Long id;
    @NotBlank
    @Size(min=1, max=255)
    private String body;
    @CreationTimestamp
    private LocalDateTime createdDate;
    @UpdateTimestamp
    private LocalDateTime updatedDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_offer_id")
    @NotNull
    private SalesOffer salesOffer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    public SalesOfferComment() {
    }

    public SalesOfferComment(Long id, String body, LocalDateTime createdDate, LocalDateTime updatedDate, SalesOffer salesOffer, User user) {
        this.id = id;
        this.body = body;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.salesOffer = salesOffer;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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

    public SalesOffer getSalesOffer() {
        return salesOffer;
    }

    public void setSalesOffer(SalesOffer salesOffer) {
        this.salesOffer = salesOffer;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
