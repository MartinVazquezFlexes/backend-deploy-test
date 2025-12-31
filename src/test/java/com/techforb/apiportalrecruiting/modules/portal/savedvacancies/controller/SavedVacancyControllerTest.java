package com.techforb.apiportalrecruiting.modules.portal.savedvacancies.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techforb.apiportalrecruiting.core.exceptions.SavedVacancyAlreadySavedException;
import com.techforb.apiportalrecruiting.core.exceptions.SavedVacancyAuthenticationException;
import com.techforb.apiportalrecruiting.core.exceptions.SavedVacancyInactiveException;
import com.techforb.apiportalrecruiting.core.exceptions.SavedVacancyNotFoundException;
import com.techforb.apiportalrecruiting.modules.portal.savedvacancies.dto.SavedVacancyDTO;
import com.techforb.apiportalrecruiting.modules.portal.savedvacancies.dto.SavedVacancyDetailsDTO;
import com.techforb.apiportalrecruiting.modules.portal.savedvacancies.service.SavedVacancyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class SavedVacancyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SavedVacancyService savedVacancyService;

    @Autowired
    private ObjectMapper objectMapper;

    private SavedVacancyDTO mockSavedVacancyDTO;
    private SavedVacancyDetailsDTO mockSavedVacancyDetailsDTO;
    private Page<SavedVacancyDTO> mockPage;

    @BeforeEach
    void setUp() {
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
                .savedDate(LocalDateTime.of(2024, 1, 1, 10, 0))
                .build();

        mockPage = new PageImpl<>(List.of(mockSavedVacancyDTO), PageRequest.of(0, 10), 1);
    }

    @Test
    void getSavedJobs_ShouldReturnPageOfSavedJobs_WhenUserIsAuthenticated() throws Exception {
        when(savedVacancyService.getSavedVacancies(any(Pageable.class))).thenReturn(mockPage);

        mockMvc.perform(get("/api/portal/saved-jobs")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "savedDate,desc"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getSavedJobs_ShouldReturnEmptyPage_WhenNoSavedJobs() throws Exception {
        Page<SavedVacancyDTO> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(savedVacancyService.getSavedVacancies(any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/api/portal/saved-jobs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));
    }

    @Test
    void getSavedJobs_WithCustomPaginationParameters() throws Exception {
        when(savedVacancyService.getSavedVacancies(any(Pageable.class))).thenReturn(mockPage);

        mockMvc.perform(get("/api/portal/saved-jobs")
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "savedDate,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getSavedJobs_ShouldReturn401_WhenUserNotAuthenticated() throws Exception {
        when(savedVacancyService.getSavedVacancies(any(Pageable.class)))
                .thenThrow(new SavedVacancyAuthenticationException("User not authenticated"));

        mockMvc.perform(get("/api/portal/saved-jobs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void saveJob_ShouldReturnCreatedSavedJob_WhenValidRequest() throws Exception {
        Long vacancyId = 1L;
        when(savedVacancyService.saveVacancy(vacancyId)).thenReturn(mockSavedVacancyDTO);

        mockMvc.perform(post("/api/portal/saved-jobs/{vacancyId}", vacancyId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.saved_vacancy_id").value(1))
                .andExpect(jsonPath("$.vacancy").exists())
                .andExpect(jsonPath("$.vacancy.role").value("Backend Developer"))
                .andExpect(jsonPath("$.saved_date").exists());
    }

    @Test
    void saveJob_ShouldReturn404_WhenVacancyNotFound() throws Exception {
        Long vacancyId = 999L;
        when(savedVacancyService.saveVacancy(vacancyId))
                .thenThrow(new SavedVacancyNotFoundException("Vacancy not found"));

        mockMvc.perform(post("/api/portal/saved-jobs/{vacancyId}", vacancyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveJob_ShouldReturn400_WhenVacancyIsInactive() throws Exception {
        Long vacancyId = 1L;
        when(savedVacancyService.saveVacancy(vacancyId))
                .thenThrow(new SavedVacancyInactiveException("Cannot save inactive vacancy"));

        mockMvc.perform(post("/api/portal/saved-jobs/{vacancyId}", vacancyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveJob_ShouldReturn409_WhenVacancyAlreadySaved() throws Exception {
        Long vacancyId = 1L;
        when(savedVacancyService.saveVacancy(vacancyId))
                .thenThrow(new SavedVacancyAlreadySavedException("Vacancy already saved"));

        mockMvc.perform(post("/api/portal/saved-jobs/{vacancyId}", vacancyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void saveJob_ShouldReturn401_WhenUserNotAuthenticated() throws Exception {
        Long vacancyId = 1L;
        when(savedVacancyService.saveVacancy(vacancyId))
                .thenThrow(new SavedVacancyAuthenticationException("User not authenticated"));

        mockMvc.perform(post("/api/portal/saved-jobs/{vacancyId}", vacancyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void saveJob_ShouldHandleInvalidVacancyId() throws Exception {
        mockMvc.perform(post("/api/portal/saved-jobs/invalid"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveJob_ShouldHandleNegativeVacancyId() throws Exception {
        Long negativeVacancyId = -1L;
        when(savedVacancyService.saveVacancy(negativeVacancyId))
                .thenThrow(new SavedVacancyNotFoundException("Vacancy not found"));

        mockMvc.perform(post("/api/portal/saved-jobs/{vacancyId}", negativeVacancyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}