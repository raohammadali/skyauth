package com.skynetauth.auth_service.models;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.skynetauth.auth_service.config.Auditable;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "distributions")
@Data
@EqualsAndHashCode(callSuper = true, exclude = "users")
public class Distribution extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    @ManyToMany(fetch = FetchType.EAGER)
    @JsonManagedReference
    @JoinTable(
        name = "distribution_users", 
        joinColumns = @JoinColumn(name = "distributions_id"),
        inverseJoinColumns = @JoinColumn(name = "users_id")
    )
    private Set<User> users = new HashSet<>();
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "divisions_id")   
    private Division divisions;

    @OneToMany(mappedBy = "distributions", fetch = FetchType.EAGER)
    private Set<SoldTo> soldTos = new HashSet<>();


}

// @Entity
// public class User {
//     @Id @GeneratedValue
//     private Long id;

//     private String name;
//     private String email;
//     private String password;

//     @ManyToMany(fetch = FetchType.EAGER)
//     private Set<Role> roles = new HashSet<>();

//     @ManyToMany(fetch = FetchType.EAGER)
//     private Set<Permission> permissions = new HashSet<>();

//     // Account relation logic (optional foreign keys or linking tables)
// }
