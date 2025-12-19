package com.techforb.apiportalrecruiting.core.services.impl;

import com.techforb.apiportalrecruiting.core.dtos.LanguageDetailDTO;
import com.techforb.apiportalrecruiting.core.dtos.ResponseDetailSkillDTO;
import com.techforb.apiportalrecruiting.core.entities.*;
import com.techforb.apiportalrecruiting.core.repositories.DetailSkillRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.detailSkill.RequestDetailSkillDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.ApplicationService;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.LanguageService;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.SkillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ActiveProfiles("test")
@SpringBootTest
class DetailSkillServiceImplTest {
	@MockitoBean
	private DetailSkillRepository detailSkillRepository;
	@MockitoSpyBean
	private DetailSkillServiceImpl detailSkillService;
	@MockitoBean
	private LanguageService languageService;
	@MockitoBean
	private SkillService skillService;
	@MockitoBean
	private ApplicationService applicationService;
	private List<DetailSkill>detailSkills;
	private List<DetailSkill>detailSkillsNull;
	private DetailSkill detailSkill;

	private DetailSkill detailSkillLanguageNull;
	private Language language;
	private Vacancy vacancy;
	private Vacancy vacancyLanguageNull;

	private Application mockApplication;
	private RequestDetailSkillDTO requestDetailSkillDTO;
	private ResponseDetailSkillDTO responseDetailSkillDTO;

	@BeforeEach
	void setUp() {
		vacancyLanguageNull=new Vacancy();
		vacancyLanguageNull.setId(2L);
		vacancyLanguageNull.setRole("qa");

		detailSkillLanguageNull=new DetailSkill();
		detailSkillLanguageNull.setVacancy(vacancyLanguageNull);
		detailSkillLanguageNull.setLanguage(null);
		detailSkillsNull =new ArrayList<>();
		detailSkillsNull.add(detailSkillLanguageNull);

		detailSkills=new ArrayList<>();
		mockApplication = new Application();
		mockApplication.setId(1L);
		vacancy = new Vacancy();
		vacancy.setId(1L);
		vacancy.setRole("dev");
		language = new Language();
		language.setId(1L);
		language.setName("English");
		language.setLanguageLevel("Advanced");

		detailSkill = new DetailSkill();
		detailSkill.setId(1L);
		detailSkill.setSkill(new Skill(1L,"description", new Category(1L,"category"),List.of(new Person())));
		detailSkill.setLanguage(language);
		detailSkill.setApplication(mockApplication);
		detailSkills.add(detailSkill);
		requestDetailSkillDTO = new RequestDetailSkillDTO(1L, 1L, 1L, 1L, true, 2, 3);

		responseDetailSkillDTO = new ResponseDetailSkillDTO(1L, "Skill1", new LanguageDetailDTO("English", "B1"), true, 2, 3);
	}

	@Test
	void findByVacancyId(){
		when(detailSkillRepository.findByVacancyIdWithRelations(1L)).thenReturn(detailSkills);
		List<DetailSkill>response=this.detailSkillService.findByVacancyId(1L);
		assertNotNull(response);
		assertEquals(1L,response.get(0).getId());
	}


	@Test
	void findLanguageByVancancyId() {
		when(detailSkillRepository.findLanguagesByVancancyId(vacancy.getId())).thenReturn(detailSkills);
		LanguageDetailDTO response = this.detailSkillService.findLanguageByVancancyId(vacancy.getId());
		assertNotNull(response);
		assertEquals(response.getLanguageLevel(), ("Advanced"));
	}
	@Test
	void findLanguageNullByVancancyId() {
		when(detailSkillRepository.findLanguagesByVancancyId(vacancyLanguageNull.getId())).thenReturn(detailSkillsNull);
		LanguageDetailDTO response = this.detailSkillService.findLanguageByVancancyId(vacancyLanguageNull.getId());
		assertNull(response);
	}
	@Test
	void getDetailSkillsByVacancyId_Success() {
		when(detailSkillRepository.getDetailSkillByVacancy_Id(1L))
				.thenReturn(List.of(detailSkill));

		List<ResponseDetailSkillDTO> result = detailSkillService.getDetailSkillsByVacancyId(1L);

		assertEquals(1, result.size());
		verify(detailSkillRepository, times(1)).getDetailSkillByVacancy_Id(1L);
	}

	@Test
	void findDbLanguageByVacancyId_Success(){
		when(detailSkillRepository.findLanguagesByVancancyId(1L)).thenReturn(detailSkills);
		Language response=this.detailSkillService.findDbLanguageByVacancyId(1L);
		assertNotNull(response);
		assertEquals("English",response.getName());

	}
	@Test
		void findDbLanguageByVacancyId_Null(){
		when(detailSkillRepository.findLanguagesByVancancyId(1L)).thenReturn(detailSkillsNull);
		Language response=this.detailSkillService.findDbLanguageByVacancyId(1L);
		assertNull(response);

	}

	@Test
	void createListDetails_UsesBeforeEachObjects() {
		when(skillService.findById(requestDetailSkillDTO.getSkillId()))
				.thenReturn(detailSkill.getSkill());
		when(languageService.findById(requestDetailSkillDTO.getLanguageId()))
				.thenReturn(language);
		List<RequestDetailSkillDTO> dtoList = List.of(requestDetailSkillDTO);

		List<DetailSkill> result = detailSkillService.createListDetails(vacancy, dtoList);

		assertNotNull(result);
		assertEquals(1, result.size());

		DetailSkill detail = result.get(0);
		assertEquals(vacancy, detail.getVacancy());
		assertEquals(detailSkill.getSkill(), detail.getSkill());
		assertEquals(requestDetailSkillDTO.getIsObligatory(), detail.getIsObligatory());
		assertEquals(requestDetailSkillDTO.getPriority(), detail.getPriority());
		assertEquals(requestDetailSkillDTO.getYearsExperience(), detail.getYearsExperience());


	}
}