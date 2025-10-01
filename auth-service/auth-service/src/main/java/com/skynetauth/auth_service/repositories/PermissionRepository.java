package com.skynetauth.auth_service.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skynetauth.auth_service.Enum.UserType;
import com.skynetauth.auth_service.models.Permission;
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    List<Permission> findAllByNameIn(List<String> names);
    List<Permission> findByUserType(UserType userType);
    Permission findByName(String name);
}