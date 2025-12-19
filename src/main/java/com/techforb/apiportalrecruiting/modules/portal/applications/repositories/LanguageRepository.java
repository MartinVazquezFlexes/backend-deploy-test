package com.techforb.apiportalrecruiting.modules.portal.applications.repositories;

import com.techforb.apiportalrecruiting.core.entities.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {
    @Query("""
    SELECT l FROM Language l
    WHERE l.name = :name
    ORDER BY
        CASE l.languageLevel
            WHEN 'Basico' THEN 1
            WHEN 'Intermedio' THEN 2
            WHEN 'Avanzado' THEN 3
            WHEN 'Nativo/Bilingüe' THEN 4
        END
""")
    List<Language> findByNameOrderByCustomLevel(String name);
}
