package com.skynetauth.auth_service.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skynetauth.auth_service.Enum.UserType;
import com.skynetauth.auth_service.models.Permission;
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    /**
 * Retrieves all Permission entities whose name matches any value in the provided list.
 *
 * @param names a list of permission names to match
 * @return a list of Permission objects whose `name` is contained in `names`; empty if none match
 */
List<Permission> findAllByNameIn(List<String> names);
    /**
 * Retrieve all permissions associated with the specified user type.
 *
 * @param userType the user type whose permissions should be retrieved
 * @return a list of Permission objects matching the given user type; empty list if none found
 */
List<Permission> findByUserType(UserType userType);
    /**
 * Finds a Permission with the exact given name.
 *
 * @param name the exact permission name to search for
 * @return the Permission with the specified name, or {@code null} if none is found
 */
Permission findByName(String name);
}