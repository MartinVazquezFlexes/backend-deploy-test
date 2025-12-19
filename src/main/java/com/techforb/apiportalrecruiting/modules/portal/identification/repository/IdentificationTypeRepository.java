package com.techforb.apiportalrecruiting.modules.portal.identification.repository;

import com.techforb.apiportalrecruiting.core.entities.IdentificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdentificationTypeRepository extends JpaRepository<IdentificationType, Long> {
}
