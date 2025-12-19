package com.techforb.apiportalrecruiting.core.repositories;

import com.techforb.apiportalrecruiting.core.entities.Vacancy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
/**
 * Repository interface for performing database operations on Vacancy entities.
 */
@Repository
public interface VacancyRepository extends JpaRepository<Vacancy,Long > {
    /**
     * Retrieves active vacancies with pagination.
     *
     * @param pageable Pagination information
     * @return Page of active Vacancy entities
     */

    @Query("SELECT v FROM Vacancy v WHERE v.active = true")
    Page<Vacancy> findAllVacanciesWithPaginationActive(Pageable pageable);

}
