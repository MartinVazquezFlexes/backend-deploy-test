package com.techforb.apiportalrecruiting.modules.portal.direction.repository;

import com.techforb.apiportalrecruiting.core.entities.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository <City,Long> {
    Optional<City> findByNameIgnoreCase(String name);
}
