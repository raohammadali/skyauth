package com.skynetauth.auth_service.models;

import java.util.HashSet;
import java.util.Set;

import com.skynetauth.auth_service.config.Auditable;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sold_tos")
public class SoldTo extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;    

    @OneToMany(mappedBy = "soldTos", fetch = FetchType.EAGER)
    private Set<ShopTo> shopTos = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "distributions_id")
    private Distribution distributions;

    //     @ManyToMany(fetch = FetchType.EAGER)
//     private Set<Role> roles = new HashSet<>();

}
