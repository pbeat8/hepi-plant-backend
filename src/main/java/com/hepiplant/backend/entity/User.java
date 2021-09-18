package com.hepiplant.backend.entity;

import com.hepiplant.backend.entity.enums.Permission;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(schema = "users", name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @NotBlank
    @Size(min=1, max=50)
    private String username;
    @NotBlank
    @Size(min=1, max=50)
    @Column(unique = true)
    private String uid;
    @NotBlank
    @Email
    private String email;
    @Enumerated(EnumType.STRING)
    private Permission permission;
    @OneToMany(mappedBy = "user")
    private List<Plant> plantList;
    @OneToMany(mappedBy = "user")
    private List<Post> postList;
    @OneToMany(mappedBy = "user")
    private List<SalesOffer> salesOfferList;

    public User() {
    }

    public User(Long id, String username, String uId, String email, Permission permission,
                List<Plant> plantList, List<Post> postList, List<SalesOffer> salesOfferList) {
        this.id = id;
        this.username = username;
        this.uid = uId;
        this.email = email;
        this.permission = permission;
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

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

}
