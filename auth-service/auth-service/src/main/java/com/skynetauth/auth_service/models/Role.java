package com.skynetauth.auth_service.models;

import java.util.List;

import com.skynetauth.auth_service.Enum.UserType;
import com.skynetauth.auth_service.config.Auditable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "roles")
@Data
@EqualsAndHashCode(callSuper = true)
public class Role extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;

    private UserType userType;

    @ManyToMany(fetch = jakarta.persistence.FetchType.EAGER)
    @jakarta.persistence.JoinTable(
        name = "role_permissions",
        joinColumns = @jakarta.persistence.JoinColumn(name = "roles_id"),
        inverseJoinColumns = @jakarta.persistence.JoinColumn(name = "permissions_id")
    )
    private List<Permission> permissions;
}