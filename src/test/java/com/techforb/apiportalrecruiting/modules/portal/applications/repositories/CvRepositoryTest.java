package com.techforb.apiportalrecruiting.modules.portal.applications.repositories;

import com.techforb.apiportalrecruiting.core.entities.Cv;
import com.techforb.apiportalrecruiting.core.entities.Person;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
@DataJpaTest
@ActiveProfiles("test")
class CvRepositoryTest{
    @Autowired
    private CvRepository cvRepository;
    @Autowired
    private TestEntityManager entityManager;

    private Long personId;

    @BeforeEach
    void setUp() {
        UserEntity user = new UserEntity();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        entityManager.persist(user);

        Person person = new Person();
        person.setUser(user);
        entityManager.persist(person);

        personId = person.getId();

        Cv cv = new Cv();
        cv.setPerson(person);
        cv.setPublicId("sad");
        cv.setVersion("1");
        entityManager.persist(cv);

        entityManager.flush();
    }

    @Test
    void findAllByPersonIdOrderByIdDesc() {
        List<Cv> cvs = cvRepository.findAllByPersonIdOrderByIdDesc(personId);

        assertEquals(1, cvs.size());
        assertEquals("sad", cvs.get(0).getPublicId());
        assertEquals("1", cvs.get(0).getVersion());
    }
}