package com.techforb.apiportalrecruiting.modules.portal.savedvacancies.service.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.entities.*;
import com.techforb.apiportalrecruiting.core.exceptions.SavedVacancyAlreadySavedException;
import com.techforb.apiportalrecruiting.core.exceptions.SavedVacancyAuthenticationException;
import com.techforb.apiportalrecruiting.core.exceptions.SavedVacancyInactiveException;
import com.techforb.apiportalrecruiting.core.exceptions.SavedVacancyNotFoundException;
import com.techforb.apiportalrecruiting.core.repositories.VacancyRepository;
import com.techforb.apiportalrecruiting.modules.portal.savedvacancies.dto.SavedVacancyDTO;
import com.techforb.apiportalrecruiting.modules.portal.savedvacancies.dto.SavedVacancyDetailsDTO;
import com.techforb.apiportalrecruiting.modules.portal.savedvacancies.mapper.SavedVacancyMapper;
import com.techforb.apiportalrecruiting.modules.portal.savedvacancies.repository.SavedVacancyRepository;
import com.techforb.apiportalrecruiting.modules.backoffice.user.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SavedVacancyServiceImplTest {

    @Mock
    private SavedVacancyRepository savedVacancyRepository;
    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private SavedVacancyMapper savedVacancyMapper;
    @Mock
    private LocalizedMessageService localizedMessageService;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @Mock
    private CustomUserDetails customUserDetails;

    @InjectMocks
    private SavedVacancyServiceImpl savedVacancyService;

    private UserEntity mockUser;
    private Person mockPerson;
    private Vacancy mockVacancy;
    private SavedVacancy mockSavedVacancy;
    private SavedVacancyDTO mockSavedVacancyDTO;
    private SavedVacancyDetailsDTO mockSavedVacancyDetailsDTO;
    private Company mockCompany;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
        
        mockPerson = new Person();
        mockPerson.setId(1L);
        
        mockUser = new UserEntity();
        mockUser.setId(1L);
        mockUser.setPerson(mockPerson);
        
        mockCompany = new Company();
        mockCompany.setId(1L);
        mockCompany.setName("Tech Company");
        
        mockVacancy = new Vacancy();
        mockVacancy.setId(1L);
        mockVacancy.setRole("Backend Developer");
        mockVacancy.setDescription("Java Developer position");
        mockVacancy.setActive(true);
        mockVacancy.setYearsExperienceRequired(3);
        mockVacancy.setCompany(mockCompany);
        
        mockSavedVacancy = SavedVacancy.builder()
                .id(1L)
                .person(mockPerson)
                .vacancy(mockVacancy)
                .savedDate(LocalDateTime.now())
                .build();
        
        mockSavedVacancyDetailsDTO = SavedVacancyDetailsDTO.builder()
                .id(1L)
                .role("Backend Developer")
                .description("Java Developer position")
                .active(true)
                .yearsExperienceRequired(3)
                .nameCompany("Tech Company")
                .direction("Buenos Aires, Buenos Aires, Argentina")
                .workModality("REMOTE")
                .skills(List.of())
                .build();
        
        mockSavedVacancyDTO = SavedVacancyDTO.builder()
                .id(1L)
                .vacancy(mockSavedVacancyDetailsDTO)
                .savedDate(LocalDateTime.now())
                .build();
        
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void getSavedVacancies_ShouldReturnPageOfSavedVacancies_WhenUserIsAuthenticated() {
        Page<SavedVacancy> savedVacancyPage = new PageImpl<>(List.of(mockSavedVacancy));
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(customUserDetails.getUserEntity()).thenReturn(mockUser);
        when(savedVacancyRepository.findByPersonIdOrderBySavedDateDesc(1L, pageable))
                .thenReturn(savedVacancyPage);
        when(savedVacancyMapper.mapToSavedVacancyDTO(mockSavedVacancy)).thenReturn(mockSavedVacancyDTO);
        
        Page<SavedVacancyDTO> result = savedVacancyService.getSavedVacancies(pageable);
        
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(mockSavedVacancyDTO, result.getContent().get(0));
        verify(savedVacancyRepository).findByPersonIdOrderBySavedDateDesc(1L, pageable);
        verify(savedVacancyMapper).mapToSavedVacancyDTO(mockSavedVacancy);
    }

    @Test
    void getSavedVacancies_ShouldThrowException_WhenUserNotAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(null);
        when(localizedMessageService.getMessage("saved.vacancy.user_not_authenticated"))
                .thenReturn("User not authenticated");
        
        SavedVacancyAuthenticationException exception = assertThrows(
                SavedVacancyAuthenticationException.class,
                () -> savedVacancyService.getSavedVacancies(pageable)
        );
        
        assertEquals("User not authenticated", exception.getMessage());
    }

    @Test
    void getSavedVacancies_ShouldThrowException_WhenUserHasNoPersonProfile() {
        mockUser.setPerson(null);
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(customUserDetails.getUserEntity()).thenReturn(mockUser);
        when(localizedMessageService.getMessage("saved.vacancy.no_person_profile"))
                .thenReturn("No person profile");
        
        SavedVacancyAuthenticationException exception = assertThrows(
                SavedVacancyAuthenticationException.class,
                () -> savedVacancyService.getSavedVacancies(pageable)
        );
        
        assertEquals("No person profile", exception.getMessage());
    }

    @Test
    void saveVacancy_ShouldSaveAndReturnDTO_WhenValidRequest() {
        Long vacancyId = 1L;
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(customUserDetails.getUserEntity()).thenReturn(mockUser);
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(mockVacancy));
        when(savedVacancyRepository.existsByPersonAndVacancy(mockPerson, mockVacancy)).thenReturn(false);
        when(savedVacancyRepository.save(any(SavedVacancy.class))).thenReturn(mockSavedVacancy);
        when(savedVacancyMapper.mapToSavedVacancyDTO(mockSavedVacancy)).thenReturn(mockSavedVacancyDTO);
        
        SavedVacancyDTO result = savedVacancyService.saveVacancy(vacancyId);
        
        assertNotNull(result);
        assertEquals(mockSavedVacancyDTO, result);
        verify(vacancyRepository).findById(vacancyId);
        verify(savedVacancyRepository).existsByPersonAndVacancy(mockPerson, mockVacancy);
        verify(savedVacancyRepository).save(any(SavedVacancy.class));
        verify(savedVacancyMapper).mapToSavedVacancyDTO(mockSavedVacancy);
    }

    @Test
    void saveVacancy_ShouldThrowException_WhenVacancyNotFound() {
        Long vacancyId = 999L;
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(customUserDetails.getUserEntity()).thenReturn(mockUser);
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.empty());
        when(localizedMessageService.getMessage("saved.vacancy.not_found", vacancyId))
                .thenReturn("Vacancy not found");
        
        SavedVacancyNotFoundException exception = assertThrows(
                SavedVacancyNotFoundException.class,
                () -> savedVacancyService.saveVacancy(vacancyId)
        );
        
        assertEquals("Vacancy not found", exception.getMessage());
        verify(vacancyRepository).findById(vacancyId);
        verify(savedVacancyRepository, never()).save(any());
    }

    @Test
    void saveVacancy_ShouldThrowException_WhenVacancyIsInactive() {
        Long vacancyId = 1L;
        mockVacancy.setActive(false);
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(customUserDetails.getUserEntity()).thenReturn(mockUser);
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(mockVacancy));
        when(localizedMessageService.getMessage("saved.vacancy.inactive"))
                .thenReturn("Cannot save inactive vacancy");
        
        SavedVacancyInactiveException exception = assertThrows(
                SavedVacancyInactiveException.class,
                () -> savedVacancyService.saveVacancy(vacancyId)
        );
        
        assertEquals("Cannot save inactive vacancy", exception.getMessage());
        verify(vacancyRepository).findById(vacancyId);
        verify(savedVacancyRepository, never()).save(any());
    }

    @Test
    void saveVacancy_ShouldThrowException_WhenVacancyAlreadySaved() {
        Long vacancyId = 1L;
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(customUserDetails.getUserEntity()).thenReturn(mockUser);
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(mockVacancy));
        when(savedVacancyRepository.existsByPersonAndVacancy(mockPerson, mockVacancy)).thenReturn(true);
        when(localizedMessageService.getMessage("saved.vacancy.already_saved"))
                .thenReturn("Vacancy already saved");
        
        SavedVacancyAlreadySavedException exception = assertThrows(
                SavedVacancyAlreadySavedException.class,
                () -> savedVacancyService.saveVacancy(vacancyId)
        );
        
        assertEquals("Vacancy already saved", exception.getMessage());
        verify(vacancyRepository).findById(vacancyId);
        verify(savedVacancyRepository).existsByPersonAndVacancy(mockPerson, mockVacancy);
        verify(savedVacancyRepository, never()).save(any());
    }

    @Test
    void saveVacancy_ShouldThrowException_WhenInvalidAuthenticationPrincipal() {
        Long vacancyId = 1L;
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("invalid_principal"); 
        when(localizedMessageService.getMessage("saved.vacancy.invalid_authentication"))
                .thenReturn("Invalid authentication");
        
        SavedVacancyAuthenticationException exception = assertThrows(
                SavedVacancyAuthenticationException.class,
                () -> savedVacancyService.saveVacancy(vacancyId)
        );
        
        assertEquals("Invalid authentication", exception.getMessage());
        verify(vacancyRepository, never()).findById(anyLong());
    }
}