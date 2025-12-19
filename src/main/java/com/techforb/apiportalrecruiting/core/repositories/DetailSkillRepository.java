package com.techforb.apiportalrecruiting.core.repositories;

import com.techforb.apiportalrecruiting.core.entities.DetailSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for performing database operations on DetailSkill entities.
 */
@Repository
public interface DetailSkillRepository extends JpaRepository<DetailSkill,Long > {
    /**
     * Retrieves DetailSkill entities associated with a specific vacancy.
     * This query does not explicitly fetch language relationships.
     *
     * @param vacancyId The ID of the vacancy
     * @return List of DetailSkill entities associated with the vacancy
     */

    @Query("SELECT ds FROM DetailSkill ds WHERE ds.vacancy.id = :vacancyId AND ds.language IS NOT NULL")
    List<DetailSkill> findLanguagesByVancancyId(@Param("vacancyId") Long vacancyId);
    @Query("SELECT ds FROM DetailSkill ds " +
            "LEFT JOIN FETCH ds.skill " +
            "LEFT JOIN FETCH ds.language " +
            "WHERE ds.vacancy.id = :vacancyId")
    List<DetailSkill> findByVacancyIdWithRelations(@Param("vacancyId") Long vacancyId);
    List<DetailSkill> getDetailSkillByVacancy_Id(Long vacancyId);
}
