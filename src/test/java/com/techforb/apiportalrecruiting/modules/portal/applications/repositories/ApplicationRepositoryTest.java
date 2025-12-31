package com.techforb.apiportalrecruiting.modules.portal.applications.repositories;

import com.techforb.apiportalrecruiting.core.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@ActiveProfiles("test")
class ApplicationRepositoryTest {
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private TestEntityManager entityManager;

    private Application application;
    private Cv cv;

    @BeforeEach
    void setUp() {
        UserEntity user = new UserEntity();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        entityManager.persist(user);

        Person person = new Person();
        person.setUser(user);
        entityManager.persist(person);
        
        Vacancy vacancy = new Vacancy();
        vacancy.setYearsExperienceRequired(5);
        Company company = new Company();
        company.setName("Test Company");
        entityManager.persist(company);
        vacancy.setCompany(company);
        UserEntity recruiter = new UserEntity();
        recruiter.setEmail("recruiter@example.com");
        recruiter.setPassword("password123");
        entityManager.persist(recruiter);
        vacancy.setRecruiter(recruiter);
        entityManager.persist(vacancy);
        cv = new Cv();
        cv.setPerson(person);
        cv.setPublicId("sad");
        cv.setVersion("1");
        entityManager.persist(cv);
        
        application = new Application();
        application.setPerson(person);
        application.setVacancy(vacancy);
    
        application.setCv(cv);
        application.setApplicationState(ApplicationState.IN_PROCESS);
        
        entityManager.persist(application);
        
        entityManager.flush();
    }
    
    @Test
    void findByCvId() {
        List<Application> found = applicationRepository.findByCvId(cv.getId());
        assertFalse(found.isEmpty());
        assertEquals(application.getId(), found.get(0).getId());
    }
}