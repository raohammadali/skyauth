package com.skynetauth.auth_service.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skynetauth.auth_service.Enum.UserType;
import com.skynetauth.auth_service.models.Role;
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByNameIn(List<String> names);
    Optional<Role> findByName(String name);
    @EntityGraph(attributePaths = {"permissions"})
    List<Role> findByUserType(UserType userType);
    List<Role> findByIdInAndUserType(List<Long> ids, UserType userType);
}