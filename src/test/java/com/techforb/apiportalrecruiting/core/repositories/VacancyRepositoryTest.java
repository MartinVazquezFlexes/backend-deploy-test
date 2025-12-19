package com.techforb.apiportalrecruiting.core.repositories;
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
@ActiveProfiles("test")
@DataJpaTest
class VacancyRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private VacancyRepository vacancyRepository;
    @BeforeEach
    void setUp() {
        Company company = new Company();
        company.setName("Test Company");
        entityManager.persist(company);
        entityManager.flush();

        UserEntity user = new UserEntity();
        user.setEmail("test23@example.com");
        user.setPassword("password123");
        entityManager.persist(user);
        entityManager.flush();

        Company company2 = new Company();
        company2.setName("Test Company");
        entityManager.persist(company2);
        entityManager.flush();

        UserEntity user2 = new UserEntity();
        user2.setEmail("test32@example.com");
        user2.setPassword("password123");
        entityManager.persist(user2);
        entityManager.flush();


        Vacancy vacancy1 = new Vacancy();
        vacancy1.setActive(true);
        vacancy1.setRole("Software Engineer");
        vacancy1.setDescription("Desarrollo de software en Java");
        vacancy1.setCompany(company);
        vacancy1.setRecruiter(user);
        vacancy1.setYearsExperienceRequired(2);
        vacancy1.setCreationDate(java.time.LocalDateTime.now());
        vacancy1.setExpirationDate(java.time.LocalDateTime.now().plusMonths(1));
        entityManager.persist(vacancy1);
        entityManager.flush();

        Vacancy vacancy2 = new Vacancy();
        vacancy2.setActive(false);
        vacancy2.setRole("Data Scientist");
        vacancy2.setDescription("Análisis de datos con Python");
        vacancy2.setCompany(company2);
        vacancy2.setRecruiter(user2);
        vacancy2.setYearsExperienceRequired(3);
        vacancy2.setCreationDate(java.time.LocalDateTime.now());
        vacancy2.setExpirationDate(java.time.LocalDateTime.now().plusMonths(1));
        entityManager.persist(vacancy2);
        entityManager.flush();
    }
    @Test
    void findAllVacanciesWithPaginationActive() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Vacancy> result = vacancyRepository.findAllVacanciesWithPaginationActive(pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(Vacancy::getActive));
    }

}

