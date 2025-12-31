package com.techforb.apiportalrecruiting.core.repositories;

import com.techforb.apiportalrecruiting.core.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@DataJpaTest
class DetailSkillRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private DetailSkillRepository detailSkillRepository;
    private Long vacancyId;
    private Long vacancyLanguageNullId;

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

        UserEntity user2 = new UserEntity();
        user2.setEmail("test232@example.com");
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
        vacancyId=vacancy1.getId();


        Vacancy vacancy2 = new Vacancy();
        vacancy2.setActive(true);
        vacancy2.setRole("Software Dev");
        vacancy2.setDescription("Desarrollo de software en Javascript");
        vacancy2.setCompany(company);
        vacancy2.setRecruiter(user);
        vacancy2.setYearsExperienceRequired(2);
        vacancy2.setCreationDate(java.time.LocalDateTime.now());
        vacancy2.setExpirationDate(java.time.LocalDateTime.now().plusMonths(1));
        entityManager.persist(vacancy2);
        entityManager.flush();
        vacancyLanguageNullId=vacancy2.getId();


        Category category2=new Category();
        Skill skill2=new Skill();
        skill2.setDescription("node");
        skill2.setCategory(category2);
        entityManager.persist(category2);
        entityManager.flush();
        entityManager.persist(skill2);
        entityManager.flush();


        DetailSkill detailSkill2=new DetailSkill();
        detailSkill2.setLanguage(null);
        detailSkill2.setVacancy(vacancy2);
        detailSkill2.setSkill(skill2);
        entityManager.persist(detailSkill2);
        entityManager.flush();

        Category category=new Category();
        Skill skill=new Skill();
        skill.setDescription("Spring Boot");
        skill.setCategory(category);
        entityManager.persist(category);
        entityManager.flush();
        entityManager.persist(skill);
        entityManager.flush();


        DetailSkill detailSkill=new DetailSkill();
        Language language =new Language();

        language.setName("English");
        detailSkill.setLanguage(language);
        entityManager.persist(language);
        entityManager.flush();

        detailSkill.setVacancy(vacancy1);
        detailSkill.setSkill(skill);
        entityManager.persist(detailSkill);
        entityManager.flush();


    }
    @Test
    void findLanguageByVancancyId() {
       List<DetailSkill> result=detailSkillRepository.findLanguagesByVancancyId(vacancyId);
        System.out.println(result);
        assertNotNull(result);
        assertEquals("English",result.get(0).getLanguage().getName());
    }

    @Test
    void findLanguageNullByVancancyId() {
        List<DetailSkill> result = detailSkillRepository.findLanguagesByVancancyId(vacancyLanguageNullId);
        List<DetailSkill>emptyList=new ArrayList<>();
        assertEquals(result, emptyList);
    }


    @Test
    void findByVacancyIdWithRelations_WhenValidId_ReturnsDetailSkillWithRelations() {

        List<DetailSkill> result = detailSkillRepository.findByVacancyIdWithRelations(vacancyId);


        assertThat(result)
                .hasSize(1)
                .first()
                .satisfies(ds -> {
                    assertThat(ds.getSkill()).isNotNull();
                    assertThat(ds.getSkill().getDescription()).isEqualTo("Spring Boot");
                    assertThat(ds.getLanguage()).isNotNull();
                    assertThat(ds.getLanguage().getName()).isEqualTo("English");
                    assertThat(ds.getVacancy().getId()).isEqualTo(vacancyId);
                });
    }
}
