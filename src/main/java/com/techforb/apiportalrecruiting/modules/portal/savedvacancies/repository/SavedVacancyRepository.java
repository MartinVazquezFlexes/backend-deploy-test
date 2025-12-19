package com.techforb.apiportalrecruiting.modules.portal.savedvacancies.repository;

import com.techforb.apiportalrecruiting.core.entities.Person;
import com.techforb.apiportalrecruiting.core.entities.SavedVacancy;
import com.techforb.apiportalrecruiting.core.entities.Vacancy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SavedVacancyRepository extends JpaRepository<SavedVacancy, Long> {

    @Query("SELECT sv FROM SavedVacancy sv " +
            "JOIN FETCH sv.vacancy v " +
            "JOIN FETCH v.company " +
            "LEFT JOIN FETCH v.detailSkills ds " +
            "LEFT JOIN FETCH ds.skill " +
            "WHERE sv.person.id = :personId " +
            "ORDER BY sv.savedDate DESC")
    Page<SavedVacancy> findByPersonIdOrderBySavedDateDesc(@Param("personId") Long personId, Pageable pageable);

    boolean existsByPersonAndVacancy(Person person, Vacancy vacancy);
}