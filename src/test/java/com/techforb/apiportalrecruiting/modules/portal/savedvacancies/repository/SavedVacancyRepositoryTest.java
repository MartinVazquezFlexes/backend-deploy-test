package com.techforb.apiportalrecruiting.modules.portal.savedvacancies.repository;

import com.techforb.apiportalrecruiting.core.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class SavedVacancyRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SavedVacancyRepository savedVacancyRepository;

    private Person testPerson;
    private Vacancy testVacancy;
    private SavedVacancy testSavedVacancy;

    @BeforeEach
    void setUp() {
        UserEntity recruiter = new UserEntity();
        recruiter.setEmail("recruiter@gmail.com");
        entityManager.persist(recruiter);

        UserEntity user = new UserEntity();
        user.setEmail("giordanofedeg@gmail.com");
        entityManager.persist(user);

        testPerson = new Person();
        testPerson.setFirstName("Fede");
        testPerson.setLastName("Gonzalez");
        testPerson.setUser(user);
        entityManager.persist(testPerson);

        Company company = new Company();
        entityManager.persist(company);

        testVacancy = new Vacancy();
        testVacancy.setRole("Software Engineer");
        testVacancy.setYearsExperienceRequired(7);
        testVacancy.setCompany(company);
        testVacancy.setRecruiter(recruiter);
        entityManager.persist(testVacancy);

        testSavedVacancy = new SavedVacancy();
        testSavedVacancy.setPerson(testPerson);
        testSavedVacancy.setVacancy(testVacancy);
        entityManager.persist(testSavedVacancy);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void findByPersonIdOrderBySavedDateDesc() {
        assertNotNull(testPerson, "La entidad Person no debería ser null");
        
        Pageable pageable = PageRequest.of(0, 10);
        
        Page<SavedVacancy> result = savedVacancyRepository.findByPersonIdOrderBySavedDateDesc(testPerson.getId(), pageable);
        
        assertNotNull(result, "El resultado no debería ser null");
        assertFalse(result.isEmpty(), "El resultado no debería estar vacío");
        assertEquals(1, result.getTotalElements(), "Debería haber 1 SavedVacancy para esta persona");
        
        SavedVacancy savedVacancy = result.getContent().get(0);
        assertNotNull(savedVacancy, "SavedVacancy no debería ser null");
        assertEquals(testPerson.getId(), savedVacancy.getPerson().getId(), "El ID de la persona debería coincidir");
        assertNotNull(savedVacancy.getVacancy(), "La vacancy no debería ser null");
        assertEquals("Software Engineer", savedVacancy.getVacancy().getRole(), "El rol debería coincidir");
    }

    @Test
    void existsByPersonAndVacancy() {
        assertNotNull(testPerson, "La entidad Person no debería ser null");
        assertNotNull(testVacancy, "La entidad Vacancy no debería ser null");
        assertEquals("Fede", testPerson.getFirstName());

        boolean exists = this.savedVacancyRepository.existsByPersonAndVacancy(testPerson, testVacancy);
        assertTrue(exists, "Se esperaba que SavedVacancy existiera para la persona y vacante dadas, pero no fue así");
    }
}