package com.skynetauth.auth_service.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.skynetauth.auth_service.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
 * Finds a user by their email address.
 *
 * @param email the email address to look up
 * @return an Optional containing the User if found, or an empty Optional if no user has the given email
 */
Optional<User> findByEmail(String email);
    /**
 * Check whether a user with the given email exists.
 *
 * @param email the user's email address to check
 * @return {@code true} if a user with the given email exists, {@code false} otherwise
 */
Boolean existsByEmail(String email);
    /**
 * Retrieve a page of users according to the provided paging and sorting parameters.
 *
 * @param pageable page and sort parameters for the query (page number, size, and sort order)
 * @return a Page of User entities for the requested page
 */
Page<User> findAll(Pageable pageable);
}
