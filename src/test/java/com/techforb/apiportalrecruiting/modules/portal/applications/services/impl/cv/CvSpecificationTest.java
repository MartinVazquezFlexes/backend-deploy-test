package com.techforb.apiportalrecruiting.modules.portal.applications.services.impl.cv;

import com.techforb.apiportalrecruiting.core.entities.*;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.CvRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@ActiveProfiles("test")
class CvSpecificationTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CvRepository cvRepository;

    private Cv cv;
    private Person person;

    @BeforeEach
    void setUp() {

        Country country = new Country();
        country.setName("Argentina");
        entityManager.persist(country);

        Province province = new Province();
        province.setCountry(country);
        entityManager.persist(province);

        City city = new City();
        city.setProvince(province);
        entityManager.persist(city);

        ZipCode zipCode = new ZipCode();
        zipCode.setName("1000");
        entityManager.persist(zipCode);

        Direction direction = new Direction();
        direction.setCity(city);
        direction.setZipCode(zipCode);
        direction.setDescription("Calle falsa 123");
        entityManager.persist(direction);

        Skill skill = new Skill();
        skill.setDescription("Java");
        entityManager.persist(skill);

         person = new Person();
        UserEntity user = new UserEntity();

        person.setUser(user);
        user.setPerson(person);

        person.setDirection(direction);
        person.setSkills(List.of(skill));

        user.setEmail("juan@example.com");
        user.setPassword("123456");
        user.setEnabled(true);

        entityManager.persist(user);

        entityManager.persist(person);

        cv = new Cv();
        cv.setPerson(person);
        cv.setIsLast(true);
        cv.setPublicId(UUID.randomUUID().toString());
        cv.setVersion("2.1");
        cv.setCreationDate(LocalDateTime.now());
        entityManager.persist(cv);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void hasCountryLike_shouldReturnCv_whenCountryMatches() {
        Specification<Cv> spec = CvSpecification.hasCountryLike("Arg");

        List<Cv> result = cvRepository.findAll(spec);

        assertEquals(1, result.size());
    }

    @Test
    void hasCountryLike_shouldReturnEmpty_whenCountryDoesNotMatch() {
        Specification<Cv> spec = CvSpecification.hasCountryLike("Chile");

        List<Cv> result = cvRepository.findAll(spec);

        assertTrue(result.isEmpty());
    }

    @Test
    void hasSkill_shouldReturnCv_whenSkillMatches() {
        Specification<Cv> spec = CvSpecification.hasSkill("Java");

        List<Cv> result = cvRepository.findAll(spec);

        assertEquals(1, result.size());
    }

    @Test
    void hasSkill_shouldReturnEmpty_whenSkillDoesNotMatch() {
        Specification<Cv> spec = CvSpecification.hasSkill("Python");

        List<Cv> result = cvRepository.findAll(spec);

        assertTrue(result.isEmpty());
    }

    @Test
    void hasIsLast_shouldReturnCv_whenTrue() {
        Specification<Cv> spec = CvSpecification.hasIsLast(true);

        List<Cv> result = cvRepository.findAll(spec);

        assertEquals(1, result.size());
    }

    @Test
    void hasIsLast_shouldIgnoreFilter_whenFalse() {
        Specification<Cv> spec = CvSpecification.hasIsLast(false);

        List<Cv> result = cvRepository.findAll(spec);

        assertEquals(1, result.size());
    }

    @Test
    void hasIdPerson_shouldReturnCv_whenPersonIdMatches() {
        Specification<Cv> spec = CvSpecification.hasIdPerson(person.getId());

        List<Cv> result = cvRepository.findAll(spec);

        assertEquals(1, result.size());
    }

    @Test
    void hasIdPerson_shouldReturnEmpty_whenPersonIdDoesNotMatch() {
        Specification<Cv> spec = CvSpecification.hasIdPerson(999L);

        List<Cv> result = cvRepository.findAll(spec);

        assertTrue(result.isEmpty());
    }
}