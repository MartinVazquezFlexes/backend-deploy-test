package com.techforb.apiportalrecruiting.modules.portal.applications.services.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.detailSkill.LanguageDetailDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.detailSkill.ResponseDetailSkillDTO;
import com.techforb.apiportalrecruiting.core.entities.*;
import com.techforb.apiportalrecruiting.core.exceptions.AlreadyAssignedCvException;
import com.techforb.apiportalrecruiting.core.exceptions.ApplicationClosedException;
import com.techforb.apiportalrecruiting.core.exceptions.CvNotOwnedException;
import com.techforb.apiportalrecruiting.core.exceptions.UnauthorizedActionException;
import com.techforb.apiportalrecruiting.modules.portal.services.DetailSkillService;
import com.techforb.apiportalrecruiting.modules.portal.services.VacancyService;
import com.techforb.apiportalrecruiting.core.services.UserService;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application.RequestApplicationDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application.RequestChangeCvApplicationDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application.ResponseApplicationDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.detailskill.RequestDetailSkillDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.ApplicationRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.CvRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.LanguageRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.CvService;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.LanguageService;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.PersonService;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.SkillService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
@ActiveProfiles("test")
@SpringBootTest
class ApplicationServiceImplTest {

	@Autowired
	private ApplicationServiceImpl applicationService;

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

	@MockitoBean
	private MultipartFile mockCvFile;

	@MockitoBean
	private PersonService personService;

	@MockitoBean
	private VacancyService vacancyService;

	@MockitoBean
	private LanguageService languageService;

	@MockitoBean
	private SkillService skillService;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private LocalizedMessageService localizedMessageService;

	private RequestApplicationDTO requestApplicationDTO;
	private Application mockApplication;
	private Cv mockCv;
	private ResponseDetailSkillDTO responseDetailSkillDTO;
	private UserEntity mockUser;
	private Person person;
	private ResponseApplicationDTO responseApplicationDTO;
	private Vacancy vacancy;
	private DetailSkill detailSkill;
	private Language language;

	@BeforeEach
	void setUp() {
		vacancy = new Vacancy();
		vacancy.setId(1L);
		vacancy.setCompany(new Company(1L, "TechForB"));
		vacancy.setExpirationDate(LocalDateTime.now().plusDays(1));

		mockUser = new UserEntity();
		mockUser.setId(1L);
		mockUser.setEmail("test@test.com");
		mockUser.setRoles(List.of(new Role(1L, "RECRUITER", List.of())));

		person = new Person();
		person.setId(1L);
		person.setUser(mockUser);

		requestApplicationDTO = new RequestApplicationDTO();
		requestApplicationDTO.setPersonId(1L);
		requestApplicationDTO.setVacancyId(1L);
		requestApplicationDTO.setComments("Comentario válido");
		requestApplicationDTO.setRequestDetailSkillDTOS(Collections.singletonList
				(new RequestDetailSkillDTO(1L, 1L, null, 1L, true, 2, 3)));

		mockCv = new Cv();
		mockCv.setId(1L);
		mockCv.setName("My CV");
		mockCv.setPerson(person);
		mockCv.setVersion("1111");
		mockCv.setPublicId("aaaa");

		language = new Language();
		language.setId(1L);
		language.setName("English");
		language.setLanguageLevel("B2");

		Category category = new Category();
		category.setId(1L);
		category.setName("Programming");

		detailSkill = new DetailSkill();
		detailSkill.setId(1L);
		detailSkill.setSkill(new Skill(1L, "Java", category, List.of(new Person())));
		detailSkill.setLanguage(language);
		detailSkill.setYearsExperience(3);

		responseApplicationDTO = new ResponseApplicationDTO();
		responseApplicationDTO.setId(1L);
		responseApplicationDTO.setApplicationState(ApplicationState.IN_PROCESS);
		responseApplicationDTO.setApplicantEmail("test@test.com");
		responseApplicationDTO.setApplicationDate(LocalDateTime.now());
		responseApplicationDTO.setUpdateDate(LocalDateTime.now());
		responseApplicationDTO.setComments("Estoy interesado");
		LanguageDetailDTO languageDetailDTO = new LanguageDetailDTO("English", "B2");
		responseDetailSkillDTO = new ResponseDetailSkillDTO(1L, null, languageDetailDTO, true, 2, 4);

		responseApplicationDTO.setResponseDetailSkillDTOS(List.of(responseDetailSkillDTO));

		mockApplication = new Application();
		mockApplication.setId(responseApplicationDTO.getId());
		mockApplication.setApplicationState(responseApplicationDTO.getApplicationState());
		mockApplication.setApplicationDate(responseApplicationDTO.getApplicationDate());
		mockApplication.setUpdateDate(responseApplicationDTO.getUpdateDate());
		mockApplication.setCv(mockCv);
		mockApplication.setPerson(person);
		mockApplication.setComments(responseApplicationDTO.getComments());
		mockApplication.setVacancy(vacancy);
		mockApplication.setDetailSkills(List.of(detailSkill));

	}

	@Test
	void shouldApplyVacancySuccessfully() {
		when(personService.getPersonById(anyLong())).thenReturn(person);
		when(vacancyService.findById(anyLong())).thenReturn(vacancy);
		when(languageService.findById(anyLong())).thenReturn(language);
		when(applicationRepository.findAllByVacancy_IdAndPerson_Id(anyLong(), anyLong())).thenReturn(List.of());
		when(applicationRepository.save(any(Application.class))).thenReturn(mockApplication);

		ResponseApplicationDTO response = applicationService.applyVacancy(requestApplicationDTO, mockCvFile);
		assertNotNull(response);
		assertEquals("Estoy interesado", response.getComments());
		assertEquals(response.getResponseDetailSkillDTOS().size(), responseApplicationDTO.getResponseDetailSkillDTOS().size());
	}

	@Test
	void shouldThrowExceptionWhenAlreadyApplied() {
		when(applicationRepository.findAllByVacancy_IdAndPerson_Id(anyLong(), anyLong())).thenReturn(List.of(mockApplication));
		assertThrows(IllegalStateException.class, () -> applicationService.applyVacancy(requestApplicationDTO, mockCvFile));
	}

	@Test
	void shouldThrowExceptionWhenCommentsAreInvalid() {
		requestApplicationDTO.setComments("<script>alert('XSS')</script>");
		assertThrows(IllegalStateException.class, () -> applicationService.applyVacancy(requestApplicationDTO, mockCvFile));
	}

	@Test
	void shouldThrowExceptionWhenCommentsContainInvalidCharacters() {
		requestApplicationDTO.setComments("Comentario con caracteres especiales @#%^&*");
		assertThrows(IllegalStateException.class, () -> applicationService.applyVacancy(requestApplicationDTO, mockCvFile));
	}

	@Test
	void applyVacancy_Fail_VacancyNotFound() {
		when(applicationRepository.findById(anyLong())).thenThrow(new EntityNotFoundException("No se ha encontrado la aplicación con el id 1"));
		assertThrows(EntityNotFoundException.class, () -> applicationService.findById(1L));
	}

	@Test
	void shouldChangeCvSuccessfully() {
		Cv oldCv = new Cv();
		mockApplication.setCv(oldCv);
		RequestChangeCvApplicationDTO request = new RequestChangeCvApplicationDTO(1L, 1L);
		when(applicationRepository.findById(1L)).thenReturn(Optional.of(mockApplication));
		when(userService.getUserFromContext()).thenReturn(mockUser);
		when(cvService.findCvById(1L)).thenReturn(mockCv);
		when(applicationRepository.save(Mockito.any())).thenReturn(mockApplication);

		ResponseApplicationDTO response = applicationService.changeCvApplication(request);

		assertNotNull(response);
		assertEquals(ApplicationState.MODIFIED, mockApplication.getApplicationState());
		assertEquals(1L, mockApplication.getCv().getId());
	}

	@Test
	void shouldThrowUnauthorizedWhenUserIsNotOwner() {
		UserEntity otherUser = new UserEntity();
		otherUser.setId(2L);
		when(applicationRepository.findById(1L)).thenReturn(Optional.of(mockApplication));
		when(userService.getUserFromContext()).thenReturn(otherUser);
		when(localizedMessageService.getMessage("user.not_authenticated")).thenReturn("No autorizado");

		RequestChangeCvApplicationDTO request = new RequestChangeCvApplicationDTO(1L, 1L);

		assertThrows(UnauthorizedActionException.class, () -> applicationService.changeCvApplication(request));
	}

	@Test
	void shouldThrowApplicationClosedWhenStateIsFinished() {
		mockApplication.setApplicationState(ApplicationState.FINISHED);
		when(applicationRepository.findById(1L)).thenReturn(Optional.of(mockApplication));
		when(userService.getUserFromContext()).thenReturn(mockUser);
		when(localizedMessageService.getMessage("application.modify.invalid_state")).thenReturn("No se puede modificar");

		RequestChangeCvApplicationDTO request = new RequestChangeCvApplicationDTO(1L, 1L);

		assertThrows(ApplicationClosedException.class, () -> applicationService.changeCvApplication(request));
	}

	@Test
	void shouldThrowApplicationClosedWhenVacancyIsExpired() {
		mockApplication.getVacancy().setExpirationDate(LocalDateTime.now().minusDays(1));
		when(applicationRepository.findById(1L)).thenReturn(Optional.of(mockApplication));
		when(userService.getUserFromContext()).thenReturn(mockUser);
		when(localizedMessageService.getMessage("application.vacancy.invalid_date")).thenReturn("Vacante expirada");

		RequestChangeCvApplicationDTO request = new RequestChangeCvApplicationDTO(1L, 1L);

		assertThrows(ApplicationClosedException.class, () -> applicationService.changeCvApplication(request));
	}

	@Test
	void shouldThrowCvNotOwnedWhenUserNotOwnerOfCv() {
		Cv otherCv = new Cv();
		Person otherPerson = new Person();
		UserEntity otherUser = new UserEntity();
		otherUser.setId(2L);
		otherPerson.setUser(otherUser);
		otherCv.setPerson(otherPerson);

		when(applicationRepository.findById(1L)).thenReturn(Optional.of(mockApplication));
		when(userService.getUserFromContext()).thenReturn(mockUser);
		when(cvService.findCvById(1L)).thenReturn(otherCv);
		when(localizedMessageService.getMessage("cv.not_owned")).thenReturn("CV no pertenece al usuario");

		RequestChangeCvApplicationDTO request = new RequestChangeCvApplicationDTO(1L, 1L);

		assertThrows(CvNotOwnedException.class, () -> applicationService.changeCvApplication(request));
	}

	@Test
	void shouldThrowAlreadyOwnedWhenSameCvProvided() {
		when(applicationRepository.findById(1L)).thenReturn(Optional.of(mockApplication));
		when(userService.getUserFromContext()).thenReturn(mockUser);
		when(cvService.findCvById(1L)).thenReturn(mockCv);
		when(localizedMessageService.getMessage("application.cv.already_owned")).thenReturn("El CV ya está asignado");

		RequestChangeCvApplicationDTO request = new RequestChangeCvApplicationDTO(1L, 1L);

		assertThrows(AlreadyAssignedCvException.class, () -> applicationService.changeCvApplication(request));
	}

}