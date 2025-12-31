package com.techforb.apiportalrecruiting.core.services.impl;

import com.techforb.apiportalrecruiting.core.config.CloudinaryConfig;
import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.config.mapper.ModelMapperUtils;
import com.techforb.apiportalrecruiting.core.dtos.*;
import com.techforb.apiportalrecruiting.core.entities.*;
import com.techforb.apiportalrecruiting.core.exceptions.UnauthorizedActionException;
import com.techforb.apiportalrecruiting.core.exceptions.VacancyNotActiveException;
import com.techforb.apiportalrecruiting.core.repositories.DetailSkillRepository;
import com.techforb.apiportalrecruiting.core.repositories.VacancyRepository;
import com.techforb.apiportalrecruiting.core.security.cloudinary.CloudinaryService;
import com.techforb.apiportalrecruiting.core.services.CompanyService;
import com.techforb.apiportalrecruiting.core.services.DetailSkillService;
import com.techforb.apiportalrecruiting.modules.backoffice.user.CustomUserDetails;
import com.techforb.apiportalrecruiting.modules.backoffice.user.UserService;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.LanguageService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringBootTest
@ActiveProfiles("test")
class VacancyServiceImplTest {
    @MockitoBean
    private VacancyRepository vacancyRepository;
    @MockitoBean
    private DetailSkillRepository detailSkillRepository;
    @MockitoSpyBean
    private VacancyServiceImpl vacancyService;
    @MockitoBean
    private DetailSkillService detailSkillService;
    @MockitoBean
    private LanguageService languageService;
    @MockitoSpyBean
    private ModelMapperUtils modelMapperUtils;
    @MockitoBean
    private CloudinaryConfig cloudinaryConfig;
    @MockitoBean
    private CloudinaryService cloudinaryService;
    @MockitoBean
    private  CompanyService companyService;
    @MockitoBean
    private  UserService userService;
    @MockitoBean
    private  LocalizedMessageService localizedMessageService;

    private Vacancy vacancy;
    private VacancyDTO vacancyDTO;
    private DetailSkill detailSkill;
    private Language language;
    private LanguageDetailDTO languageDetailDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        vacancy =new Vacancy();
        vacancy.setId(1L);
        vacancy.setRole("dev");
        pageable = PageRequest.of(0, 10);
        vacancyDTO = new VacancyDTO();
        vacancyDTO.setId(1L);
        vacancyDTO.setRole("dev");
        language = new Language();
        language.setName("English");
        language.setLanguageLevel("Advanced");

        detailSkill = new DetailSkill();
        detailSkill.setId(1L);
        detailSkill.setLanguage(language);
        languageDetailDTO = new LanguageDetailDTO();
        languageDetailDTO.setName("English");
        languageDetailDTO.setLanguageLevel("Advanced");
    }

    @Test
    void getVacanciesActiveWithLanguage() {
        List<Vacancy> vacancies = Collections.singletonList(vacancy);
        Page<Vacancy> vacancyPage = new PageImpl<>(vacancies, pageable, vacancies.size());

        when(vacancyRepository.findAllVacanciesWithPaginationActive(pageable)).thenReturn(vacancyPage);
        when(detailSkillService.findLanguageByVancancyId(vacancy.getId())).thenReturn(languageDetailDTO);

        Page<VacancyDTO> result = vacancyService.getVacanciesActiveWithLanguage(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        VacancyDTO resultDTO = result.getContent().get(0);
        assertEquals(vacancyDTO.getId(), resultDTO.getId());
        assertEquals(vacancyDTO.getRole(), resultDTO.getRole());

        assertNotNull(resultDTO.getLanguage());
        assertEquals("English", resultDTO.getLanguage().getName());
        assertEquals("Advanced", resultDTO.getLanguage().getLanguageLevel());

        verify(vacancyRepository).findAllVacanciesWithPaginationActive(pageable);
        verify(detailSkillService).findLanguageByVancancyId(vacancy.getId());
    }

    @Test
    void getVacanciesActiveWithLanguage_emptyResults() {
        Page<Vacancy> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(vacancyRepository.findAllVacanciesWithPaginationActive(pageable)).thenReturn(emptyPage);
        when(localizedMessageService.getMessage("vacancy.active_not_found")).thenReturn("No se encontraron vacantes activas.");

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            vacancyService.getVacanciesActiveWithLanguage(pageable);
        });

        assertEquals("No se encontraron vacantes activas.", exception.getMessage());
        verify(vacancyRepository).findAllVacanciesWithPaginationActive(pageable);
    }

    @Test
    void getDetailsVacancy_ValidId_ReturnsDTOWithSkills() {

        Long id = 1L;
        Vacancy mockVacancy = new Vacancy();
        mockVacancy.setId(id);
        mockVacancy.setActive(true);
        List<DetailSkill> mockSkills = List.of(new DetailSkill());

        when(vacancyRepository.findById(id)).thenReturn(Optional.of(mockVacancy));
        when(detailSkillService.findByVacancyId(id)).thenReturn(mockSkills);


        VacancyDetailsDTO result = vacancyService.getDetailsVacancyById(id);

        assertNotNull(result);
        assertEquals(1, result.getSkills().size());
        verify(vacancyRepository).findById(id);
        verify(detailSkillService).findByVacancyId(id);
    }

    @Test
    void getDetailsVacancy_InactiveVacancy_ThrowsException() {

        Long id = 2L;
        Vacancy mockVacancy = new Vacancy();
        mockVacancy.setId(id);
        mockVacancy.setActive(false);

        when(vacancyRepository.findById(id)).thenReturn(Optional.of(mockVacancy));


        assertThrows(VacancyNotActiveException.class, () -> vacancyService.getDetailsVacancyById(id));
        verify(vacancyRepository).findById(id);
    }
  
    @Test
    void findById_ShouldReturnLanguage_WhenExists() {
        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));

        Vacancy result = vacancyService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("dev", result.getRole());
        verify(vacancyRepository, times(1)).findById(1L);
    }

    @Test
    void findById_ShouldThrowRuntimeException_WhenNotFound() {
        when(vacancyRepository.findById(2L)).thenReturn(Optional.empty());
        when(localizedMessageService.getMessage("vacancy.not_found_by_id")).thenReturn("Vacante no encontrada.");

        Exception exception = assertThrows(EntityNotFoundException.class, () -> vacancyService.findById(2L));

        assertEquals("Vacante no encontrada.", exception.getMessage());
        verify(vacancyRepository, times(1)).findById(2L);
    }

    @Test
    void updateVacancyShouldReturnUpdatedVacancy() {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("RECRUITER"));

        UserEntity recruiter = new UserEntity();
        recruiter.setId(100L);

        CustomUserDetails userDetails = new CustomUserDetails(recruiter); // user logueado con ese recruiter

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        VacancyRequestUpdateDTO updateDTO;

        vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setRole("Old Role");
        vacancy.setDescription("Old Description");
        vacancy.setActive(true);
        vacancy.setYearsExperienceRequired(2);
        vacancy.setRecruiter(recruiter);

        updateDTO = new VacancyRequestUpdateDTO();
        updateDTO.setRole("New Role");
        updateDTO.setDescription("New Description");
        updateDTO.setYearsExperienceRequired(5);
        updateDTO.setLanguageLevel("B2");

        language = new Language();
        language.setLanguageLevel("B1");

        vacancyDTO = new VacancyDTO();
        languageDetailDTO = new LanguageDetailDTO();

        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(vacancy));
        when(detailSkillService.findDbLanguageByVacancyId(1L)).thenReturn(language);
        when(languageService.saveLanguage(language)).thenReturn(language);
        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
        when(modelMapperUtils.map(vacancy, VacancyDTO.class)).thenReturn(vacancyDTO);
        when(modelMapperUtils.map(language, LanguageDetailDTO.class)).thenReturn(languageDetailDTO);

        VacancyDTO result = vacancyService.updateVacancy(updateDTO, 1L);

        assertEquals(vacancyDTO, result);
        assertEquals(languageDetailDTO, result.getLanguage());

        verify(vacancyRepository).save(vacancy);
        verify(languageService).saveLanguage(language);
        verify(modelMapperUtils).map(vacancy, VacancyDTO.class);
        verify(modelMapperUtils).map(language, LanguageDetailDTO.class);

        assertEquals("New Role", vacancy.getRole());
        assertEquals("New Description", vacancy.getDescription());
        assertEquals(5, vacancy.getYearsExperienceRequired());
        assertEquals("B2", language.getLanguageLevel());

        SecurityContextHolder.clearContext();
    }
  
    @Test
    void createVacancy_WithRecruiterRole_ReturnsVacancyDetailsDTO() {
        RequestFullVacancyDTO dto = new RequestFullVacancyDTO();
        dto.setIdCompany(1L);
        dto.setIdRecruiter(1L);
        dto.setRole("Backend");
        dto.setActive(true);
        dto.setDetailsSkills(Collections.emptyList());

        Company company = new Company();
        company.setId(1L);
        UserEntity recruiter = new UserEntity();
        recruiter.setId(1L);
        recruiter.setRoles(List.of(new Role(1L,"RECRUITER", List.of())));

        Vacancy savedVacancy = new Vacancy();
        savedVacancy.setActive(true);
        savedVacancy.setId(1L);

        VacancyDetailsDTO expectedResponse = new VacancyDetailsDTO();
        expectedResponse.setId(1L);

        when(vacancyRepository.findById(1L)).thenReturn(Optional.of(savedVacancy));
        when(companyService.findById(1L)).thenReturn(company);
        when(userService.getUserFromContext()).thenReturn(recruiter);
        when(detailSkillService.createListDetails(any(), any())).thenReturn(Collections.emptyList());
        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(savedVacancy);
        when(vacancyService.getDetailsVacancyById(1L)).thenReturn(expectedResponse);

        // Simula contexto de seguridad
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("RECRUITER"));
        Authentication auth = new UsernamePasswordAuthenticationToken("user", "pass", authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);

        VacancyDetailsDTO result = vacancyService.create(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(vacancyRepository).save(any(Vacancy.class));
    }

    @Test
    void createVacancy_WithoutRecruiterRole_ThrowsUnauthorized() {
        RequestFullVacancyDTO dto = new RequestFullVacancyDTO();
        dto.setIdCompany(1L);
        dto.setIdRecruiter(1L);
        UserEntity user = new UserEntity();
        user.setRoles(List.of(new Role(1L,"APPLICANT", List.of())));
        user.setId(1L);

        when(userService.getUserFromContext()).thenReturn(user);
        // Usuario sin permisos
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("USER"));
        Authentication auth = new UsernamePasswordAuthenticationToken("user", "pass", authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);

        UnauthorizedActionException exception = assertThrows(UnauthorizedActionException.class, () -> {
            vacancyService.create(dto);
        });

        assertEquals(localizedMessageService.getMessage("user.without_permissions"), exception.getMessage());
    }
  
    @Test
    void disableVacancy() {
        Vacancy vacancyToSearch = new Vacancy();
        vacancyToSearch.setId(1L);
        vacancyToSearch.setActive(true);
        Long vacancyId = 1L;
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancyToSearch));
        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(vacancyToSearch);
        VacancyNotActiveDTO response = vacancyService.disableVacancy(vacancyId);
        assertNotNull(response);
        assertEquals(vacancyId, response.getId());
        assertFalse(response.getActive());
        verify(vacancyRepository).findById(vacancyId);
        verify(vacancyRepository).save(any(Vacancy.class));
    }

    @Test
    void disableVacancyAlreadyDisabled() {
        Vacancy vacancyToSearch = new Vacancy();
        vacancyToSearch.setId(1L);
        vacancyToSearch.setActive(false);
        Long vacancyId = 1L;
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancyToSearch));
        when(localizedMessageService.getMessage("vacancy.disable")).thenReturn("La vacante ya se encuentra desactivada.");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> vacancyService.disableVacancy(vacancyId));
        assertEquals("La vacante ya se encuentra desactivada.",exception.getMessage());
        verify(vacancyRepository).findById(vacancyId);
        verify(vacancyRepository, never()).save(any(Vacancy.class));
    }
}
