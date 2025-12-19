package com.techforb.apiportalrecruiting.modules.portal.applications.repositories;

import com.techforb.apiportalrecruiting.core.entities.Cv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface CvRepository extends JpaRepository<Cv, Long> , JpaSpecificationExecutor<Cv>{
    @Modifying
    @Query("DELETE FROM Cv c WHERE c.person.id = :personId")
    void deleteByPersonId(@Param("personId") Long personId);

    List<Cv> findByPersonId(Long id);



    @Query("SELECT c FROM Cv c WHERE c.person.id = :personId ORDER BY c.id DESC")
    List<Cv> findAllByPersonIdOrderByIdDesc(@Param("personId") Long personId);

    Optional<Cv> findByPersonIdAndIsLastTrue(Long personId);
}
