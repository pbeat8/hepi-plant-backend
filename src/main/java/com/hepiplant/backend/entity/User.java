package com.hepiplant.backend.entity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Entity
@Table(schema = "users", name="users")
@SequenceGenerator(name = "users.users_seq", allocationSize = 1)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users.users_seq")
    private Long id;
    @NotBlank
    @Size(min=1, max=50)
    private String username;
    @NotBlank
    @Size(min=1, max=255)
    @Column(unique = true)
    private String uid;
    @NotBlank
    @Email
    @Column(unique = true)
    private String email;
    private boolean notifications;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(schema = "users", name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;
    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private List<Plant> plantList;
    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private List<Post> postList;
    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private List<SalesOffer> salesOfferList;

    public User() {
    }

    public User(Long id, String username, String uId, String email, boolean notifications, Set<Role> roles,
                List<Plant> plantList, List<Post> postList, List<SalesOffer> salesOfferList) {
        this.id = id;
        this.username = username;
        this.uid = uId;
        this.email = email;
        this.notifications = notifications;
        this.roles = roles;
        this.plantList = plantList;
        this.postList = postList;
        this.salesOfferList = salesOfferList;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
