package com.hepiplant.backend.entity;

import javax.persistence.*;

@Entity
@Table(schema = "users", name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String username;
    private String login;
    private String password;
    private String email;
    @Enumerated(EnumType.String)
    private Permission permission;
}
