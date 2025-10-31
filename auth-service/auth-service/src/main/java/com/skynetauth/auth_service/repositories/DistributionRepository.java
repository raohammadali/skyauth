package com.skynetauth.auth_service.repositories;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skynetauth.auth_service.models.Distribution;

@Repository
public interface DistributionRepository extends JpaRepository<Distribution, Long> {
    /**
 * Finds distributions whose name matches any value in the provided list.
 *
 * @param names list of distribution names to match
 * @return a list of Distribution entities whose `name` equals any value in `names`; an empty list if none match
 */
List<Distribution> findByNameIn(List<String> names);
}
