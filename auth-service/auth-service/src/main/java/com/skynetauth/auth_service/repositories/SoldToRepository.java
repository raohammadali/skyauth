package com.skynetauth.auth_service.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skynetauth.auth_service.models.SoldTo;

@Repository
public interface SoldToRepository extends JpaRepository<SoldTo, Long> {
    Page<SoldTo> findAllByDistributionsId(Long distributionsId, Pageable pageable);
    Optional<SoldTo> findByName(String name);
}
