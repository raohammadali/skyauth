package com.skynetauth.auth_service.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skynetauth.auth_service.Enum.UserType;
import com.skynetauth.auth_service.models.Role;
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
 * Finds all Role entities whose name is contained in the provided list of names.
 *
 * @param names list of role names to match
 * @return list of matching Role entities; empty list if no matches
 */
List<Role> findByNameIn(List<String> names);
    /**
 * Finds a role by its exact name.
 *
 * @param name the exact role name to search for
 * @return an Optional containing the matching Role if present, otherwise an empty Optional
 */
Optional<Role> findByName(String name);
    /**
 * Retrieve all Role entities from the data store.
 *
 * @return a list of all stored Role entities; empty list if none exist
 */
List<Role> findAll();
    /**
 * Retrieves all roles associated with the specified user type.
 *
 * @param userType the user type whose roles should be returned
 * @return a list of Role entities associated with the provided user type (empty if none)
 */
List<Role> findByUserType(UserType userType);
}