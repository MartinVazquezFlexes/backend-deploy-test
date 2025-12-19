package com.techforb.apiportalrecruiting.modules.portal.direction.repository;

import com.techforb.apiportalrecruiting.core.entities.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProvinceRepository extends JpaRepository <Province,Long> {
    Optional<Province> findByNameIgnoreCase(String name);
}
