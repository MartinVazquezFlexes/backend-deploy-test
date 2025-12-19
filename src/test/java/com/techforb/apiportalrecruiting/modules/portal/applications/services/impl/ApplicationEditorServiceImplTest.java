package com.techforb.apiportalrecruiting.modules.portal.applications.services.impl;

import com.techforb.apiportalrecruiting.core.entities.*;
import com.techforb.apiportalrecruiting.core.services.DetailSkillService;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.ApplicationDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.ApplicationModified;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.DetailSkillUpdateDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.ApplicationRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.CvRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.LanguageRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.CvService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ActiveProfiles("test")
@SpringBootTest
class ApplicationEditorServiceImplTest {

    @Autowired
    private ApplicationEditorServiceImpl applicationService;

    @MockitoBean
    private ApplicationRepository applicationRepository;

    @MockitoBean
    private CvService cvService;

    @MockitoBean
    private CvRepository cvRepository;

    @MockitoBean
    private LanguageRepository languageRepository;

    @MockitoBean
    private DetailSkillService detailSkillService;

    private Person person;
    private Application mockApplication;
    private Cv mockCv;
    private UserEntity mockUser;
    private Vacancy vacancy;
    private DetailSkill detailSkill;
    private Language language;

    @BeforeEach
    void setUp(){
        vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setCompany(new Company(1L, "TechForB"));

        mockUser = new UserEntity();
        mockUser.setId(1L);
        mockUser.setEmail("test@test.com");

        person = new Person();
        person.setId(1L);
        person.setUser(mockUser);

        mockCv = new Cv();
        mockCv.setId(1L);
        mockCv.setVersion("12323254");
        mockCv.setName("My CV");
        mockCv.setPublicId("publicId");

        language = new Language();
        language.setId(1L);
        language.setName("English");
        language.setLanguageLevel("B2");

        Category category = new Category();
        category.setId(1L);
        category.setName("Programming");

        detailSkill = new DetailSkill();
        detailSkill.setId(1L);
        detailSkill.setSkill(new Skill(1L, "Java", category,List.of(new Person())));
        detailSkill.setLanguage(language);
        detailSkill.setYearsExperience(3);

        mockApplication = new Application();
        mockApplication.setId(1L);
        mockApplication.setApplicationState(ApplicationState.IN_PROCESS);
        mockApplication.setApplicationDate(LocalDateTime.now());
        mockApplication.setCv(mockCv);
        mockApplication.setPerson(person);
        mockApplication.setComments("Initial comment");
        mockApplication.setVacancy(vacancy);
        mockApplication.setDetailSkills(List.of(detailSkill));
    }

    @Test
    void modifyApplication() {
        ApplicationModified modified = new ApplicationModified();
        modified.setComments("Updated comment");
        modified.setApplicationState(ApplicationState.MODIFIED);

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(mockApplication));
        when(applicationRepository.save(any(Application.class))).thenReturn(mockApplication);

        ApplicationDTO result = applicationService.modifyApplication(1L, modified);

        assertNotNull(result);
        assertEquals("Updated comment", result.getComments());
        assertEquals(ApplicationState.MODIFIED, result.getApplicationState());
        verify(applicationRepository, times(1)).findById(1L);
        verify(applicationRepository, times(1)).save(mockApplication);
    }

    @Test
    void modifyApplicationException() {//cuando no hay aplicaciones, no se puede modificar nada.
        ApplicationModified modified = new ApplicationModified();
        when(applicationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            applicationService.modifyApplication(1L, modified);
        });
    }


    //no se puede modificar una aplicacion cancelada
    @Test
    void UnableToModifyCanceledApplication() {
        mockApplication.setApplicationState(ApplicationState.CANCELED);
        ApplicationModified modified = new ApplicationModified();

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(mockApplication));

        assertThrows(IllegalStateException.class, () -> {
            applicationService.modifyApplication(1L, modified);
        });
    }

    @Test
    void modifyApplication_Cv() {
        ApplicationModified modified = new ApplicationModified();
        modified.setCvId(2L);

        Cv newCv = new Cv();
        newCv.setId(2L);
        newCv.setPublicId("publicId");
        newCv.setVersion("12323222");

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(mockApplication));
        when(cvRepository.findById(2L)).thenReturn(Optional.of(newCv));
        when(applicationRepository.save(any(Application.class))).thenReturn(mockApplication);

        ApplicationDTO result = applicationService.modifyApplication(1L, modified);

        assertEquals(2L, result.getCv().getId());
        verify(cvRepository, times(1)).findById(2L);
    }

    @Test
    void modifyApplication_YearsExperience() {
        DetailSkillUpdateDTO skillUpdate = new DetailSkillUpdateDTO(1L, 5);
        ApplicationModified modified = new ApplicationModified();
        modified.setDetailSkills(List.of(skillUpdate));

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(mockApplication));
        when(applicationRepository.save(any(Application.class))).thenReturn(mockApplication);

        ApplicationDTO result = applicationService.modifyApplication(1L, modified);

        assertEquals(5, mockApplication.getDetailSkills().get(0).getYearsExperience());
        verify(applicationRepository).save(mockApplication);
    }

    @Test
    void modifyApplication_Language() {
        ApplicationModified modified = new ApplicationModified();
        modified.setLanguageId(2L);

        Language newLanguage = new Language(2L, "C1", "Spanish");
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(mockApplication));
        when(languageRepository.findById(2L)).thenReturn(Optional.of(newLanguage));
        when(applicationRepository.save(any(Application.class))).thenReturn(mockApplication);

        System.out.println("Before: " + mockApplication.getDetailSkills().get(0).getLanguage().getName());
        applicationService.modifyApplication(1L, modified);
        System.out.println("After: " + mockApplication.getDetailSkills().get(0).getLanguage().getName());


        assertEquals("Spanish", mockApplication.getDetailSkills().get(0).getLanguage().getName());
        verify(languageRepository).findById(2L);
    }

    @Test
    void modifyApplication_ApplicationState() {
        ApplicationModified modified = new ApplicationModified();
        modified.setComments("Nuevo comentario");
        modified.setApplicationState(ApplicationState.MODIFIED);
        modified.setDetailSkills(List.of(new DetailSkillUpdateDTO(1L, 5)));

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(mockApplication));
        when(applicationRepository.save(any(Application.class))).thenReturn(mockApplication);

        ApplicationDTO result = applicationService.modifyApplication(1L, modified);

        assertEquals("Nuevo comentario", result.getComments());
        assertEquals(ApplicationState.MODIFIED, result.getApplicationState());
        //assertEquals(5, mockApplication.getDetailSkills().get(0).getYearsExperience());
    }

    //no se puede modificar el estado de una aplicacion cancelada
    @Test
    void UnableToModifyCanceledApplicationState() {
        // Configurar aplicación en estado CANCELED
        mockApplication.setApplicationState(ApplicationState.CANCELED);

        ApplicationModified modified = new ApplicationModified();
        modified.setApplicationState(ApplicationState.MODIFIED); // Intentar modificar

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(mockApplication));

        assertThrows(IllegalStateException.class, () -> {
            applicationService.modifyApplication(1L, modified);
        });

        verify(applicationRepository, never()).save(any());
    }

    @Test
    void modifyApplicationPartialFields() {
        String originalComment = mockApplication.getComments();
        ApplicationModified modified = new ApplicationModified();
        modified.setApplicationState(ApplicationState.IN_PROCESS);

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(mockApplication));
        when(applicationRepository.save(any(Application.class))).thenReturn(mockApplication);

        ApplicationDTO result = applicationService.modifyApplication(1L, modified);

        assertEquals(originalComment, result.getComments());
        assertEquals(ApplicationState.IN_PROCESS, result.getApplicationState());
    }

    //obtener las aplicacion con todas las relaciones
    @Test
    void getApplicationByApplicantId_ShouldMapCompleteRelations() {
        when(applicationRepository.findByPersonId(1L)).thenReturn(List.of(mockApplication));

        List<ApplicationDTO> result = applicationService.getApplicationByApplicantId(1L);

        assertFalse(result.get(0).getDetailSkill().isEmpty());
        assertNotNull(result.get(0).getVacancy());
        assertNotNull(result.get(0).getCv());
        assertFalse(result.get(0).getLanguage().isEmpty());
    }

    //Pruebas para cancelar postulación
    @Test
    void cancelApplication() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(mockApplication));
        when(applicationRepository.save(any(Application.class))).thenReturn(mockApplication);

        ApplicationDTO result = applicationService.modifyStateApplication(1L, ApplicationState.CANCELED);

        assertEquals(ApplicationState.CANCELED, result.getApplicationState());
        verify(applicationRepository, times(1)).save(mockApplication);
    }

    @Test
    void unableToCancelFinishedApplication() {
        mockApplication.setApplicationState(ApplicationState.FINISHED);
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(mockApplication));

        assertThrows(IllegalStateException.class, () -> {
            applicationService.modifyStateApplication(1L, ApplicationState.CANCELED);
        });
    }

    @Test
    void unabletoModifyANotFoundApplication() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            applicationService.modifyStateApplication(1L, ApplicationState.CANCELED);
        });
    }
}