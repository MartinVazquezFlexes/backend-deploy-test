package com.techforb.apiportalrecruiting.modules.portal.direction.repository;

import com.techforb.apiportalrecruiting.core.entities.ZipCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ZipCodeRepository extends JpaRepository <ZipCode, Long> {
    Optional<ZipCode> findByName(String name);
}
