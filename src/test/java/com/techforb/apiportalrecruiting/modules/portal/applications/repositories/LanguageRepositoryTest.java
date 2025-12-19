package com.techforb.apiportalrecruiting.modules.portal.applications.repositories;

import com.techforb.apiportalrecruiting.core.entities.Language;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@ActiveProfiles("test")
class LanguageRepositoryTest {
    @Autowired
    private EntityManager em;
    @Autowired
    private LanguageRepository languageRepository;
    @BeforeEach
    void setUp() {
        // Seed custom ordered levels for name "Ingles" in random order
        em.persist(Language.builder().name("Ingles").languageLevel("Intermedio").build());
        em.persist(Language.builder().name("Ingles").languageLevel("Nativo/Bilingüe").build());
        em.persist(Language.builder().name("Ingles").languageLevel("Basico").build());
        em.persist(Language.builder().name("Ingles").languageLevel("Avanzado").build());
        em.flush();
    }
    @Test
    void findByNameOrderByCustomLevel() {
        var result = languageRepository.findByNameOrderByCustomLevel("Ingles");
        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals("Basico", result.get(0).getLanguageLevel());
        assertEquals("Intermedio", result.get(1).getLanguageLevel());
        assertEquals("Avanzado", result.get(2).getLanguageLevel());
        assertEquals("Nativo/Bilingüe", result.get(3).getLanguageLevel());
    }
}