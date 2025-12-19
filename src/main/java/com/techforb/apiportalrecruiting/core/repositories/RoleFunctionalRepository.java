package com.techforb.apiportalrecruiting.core.repositories;

import com.techforb.apiportalrecruiting.core.entities.RoleFunctional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleFunctionalRepository extends JpaRepository<RoleFunctional, Long> {
}
