package com.techforb.apiportalrecruiting.modules.portal.phone.repository;

import com.techforb.apiportalrecruiting.core.entities.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, Long> {

    @Modifying
    @Query("DELETE FROM Phone p WHERE p.person.id = :personId")
    void deleteByPersonId(@Param("personId") Long personId);
    
    Optional<Phone> findByPersonId(Long personId);
}
