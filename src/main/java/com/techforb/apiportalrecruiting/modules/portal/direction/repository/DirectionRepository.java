package com.techforb.apiportalrecruiting.modules.portal.direction.repository;

import com.techforb.apiportalrecruiting.core.entities.Direction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectionRepository extends JpaRepository <Direction,Long> {

}
