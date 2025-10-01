package com.skynetauth.auth_service.models;

import com.skynetauth.auth_service.Enum.UserType;
import com.skynetauth.auth_service.config.Auditable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "permissions")
@NoArgsConstructor
public class Permission extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private UserType userType;

    public Permission(String name, UserType userType) {
        this.name = name;
        this.userType = userType;
    }
}
