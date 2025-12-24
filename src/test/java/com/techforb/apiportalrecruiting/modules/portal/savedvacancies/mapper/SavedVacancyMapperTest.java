package com.techforb.apiportalrecruiting.modules.portal.savedvacancies.mapper;

import com.techforb.apiportalrecruiting.core.entities.*;
import com.techforb.apiportalrecruiting.modules.portal.savedvacancies.dto.SavedVacancyDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class SavedVacancyMapperTest {

    @InjectMocks
    private SavedVacancyMapper savedVacancyMapper;

    private SavedVacancy savedVacancy;
    private Vacancy vacancy;

    @BeforeEach
    void setUp(){
        LocalDateTime localDateTime=LocalDateTime.of(2025,12,10,10,20);
        Permission permission=new Permission(1L,"TODO");
        List<Permission>permissionList=new ArrayList<>();
        permissionList.add(permission);
        Role role=new Role(1L,"ADMIN",permissionList);
        List<Role>roleList=new ArrayList<>();
        roleList.add(role);

        Person person = new Person();
        person.setId(1L);
        List<Person>people=new ArrayList<>();
        people.add(person);
        ZipCode zipCode=new ZipCode(1L,"5151");
        City city=new City(1L,"Cordoba",new Province(1L,"Cordoba",new Country(1L,"Argentina")));
        Category category = new Category(1L, "PROGRAMMER");

        Direction direction = new Direction(1L, "Amazing City", city, zipCode);

        Skill skill=new Skill(1L,"WORK", category,people);

        DetailSkill detailSkill=new DetailSkill(1L,vacancy,skill,new Application(1L, person,"Comments",localDateTime
        ,localDateTime,null,vacancy,null,null),new Language(1L,"ENGLISH","A1"),true,
                1,3);
        List<DetailSkill>detailSkills=new ArrayList<>();
        detailSkills.add(detailSkill);
        UserEntity user = new UserEntity(1L, "juan@example.com", "123456", true, true, true
                , true, roleList, person);

        Company company = new Company(1L, "Movistar");

        vacancy=new Vacancy();
        vacancy.setActive(true);
        vacancy.setId(1L);
        vacancy.setRole("ADMIN");
        vacancy.setDescription("Vacante");
        vacancy.setYearsExperienceRequired(5);
        vacancy.setWorkModality(WorkModality.HYBRID);
        vacancy.setCompany(company);
        vacancy.setExpirationDate(localDateTime);
        vacancy.setRecruiter(user);
        vacancy.setActive(true);
        vacancy.setDirection(direction);
        vacancy.setDetailSkills(detailSkills);

        savedVacancy=new SavedVacancy();
        savedVacancy.setId(1L);
        savedVacancy.setSavedDate(localDateTime);
        savedVacancy.setVacancy(vacancy);
        savedVacancy.setPerson(person);
    }


    @Test
    void mapToSavedVacancyDTO() {
        SavedVacancyDTO response=this.savedVacancyMapper.mapToSavedVacancyDTO(savedVacancy);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1L,response.getId());
        Assertions.assertEquals("Vacante",vacancy.getDescription());
    }
    @Test
    void mapToSavedVacancyDTO_NULL() {
        Assertions.assertNull(this.savedVacancyMapper.mapToSavedVacancyDTO(null));
    }

}