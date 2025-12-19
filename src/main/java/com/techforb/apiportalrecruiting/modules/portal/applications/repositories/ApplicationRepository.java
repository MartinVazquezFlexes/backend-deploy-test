package com.techforb.apiportalrecruiting.modules.portal.applications.repositories;

import com.techforb.apiportalrecruiting.core.entities.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
@Repository
public interface ApplicationRepository extends JpaRepository <Application, Long> {
    List<Application>findByPersonId(Long id);
    List<Application> findAllByVacancy_IdAndPerson_Id(Long vacancyId, Long personId);
    List<Application> findByCvId(Long cvId);
}