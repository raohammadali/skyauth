package com.skynetauth.auth_service.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    @ManyToMany(mappedBy = "distributions")
    @JsonManagedReference
    private Set<User> users = new HashSet<>();

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "divisions_id")   
    private Division divisions;

    @OneToMany(mappedBy = "distributions", fetch = FetchType.LAZY)
    private List<SoldTo> soldTos = new ArrayList<>();


}