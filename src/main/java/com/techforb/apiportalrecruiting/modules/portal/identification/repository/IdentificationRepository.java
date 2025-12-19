package com.techforb.apiportalrecruiting.modules.portal.identification.repository;

import com.techforb.apiportalrecruiting.core.entities.Identification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IdentificationRepository extends JpaRepository<Identification, Long> {
    @Modifying
    @Query("DELETE FROM Identification i WHERE i.person.id = :personId")
    void deleteByPersonId(@Param("personId") Long personId);
}
