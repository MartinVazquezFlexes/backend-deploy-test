package com.techforb.apiportalrecruiting.core.controllers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techforb.apiportalrecruiting.core.dtos.LanguageDetailDTO;
import com.techforb.apiportalrecruiting.core.dtos.RequestFullVacancyDTO;
import com.techforb.apiportalrecruiting.core.dtos.VacancyDTO;
import com.techforb.apiportalrecruiting.core.dtos.VacancyRequestUpdateDTO;
import com.techforb.apiportalrecruiting.core.exceptions.UnauthorizedActionException;
import com.techforb.apiportalrecruiting.core.dtos.VacancyNotActiveDTO;
import com.techforb.apiportalrecruiting.core.entities.Vacancy;
import com.techforb.apiportalrecruiting.core.services.VacancyService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import com.techforb.apiportalrecruiting.core.dtos.ResponseDetailSkillDTO;
import com.techforb.apiportalrecruiting.core.dtos.VacancyDetailsDTO;
import com.techforb.apiportalrecruiting.core.exceptions.VacancyNotActiveException;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.Collections;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class VacancyControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private VacancyService vacancyService;
    private VacancyDTO vacancyDTO;
    private Page<VacancyDTO> vacancyPage;
    @Autowired
    private ObjectMapper objectMapper;
    private VacancyNotActiveDTO vacancyNotActiveDTO;
    private VacancyNotActiveDTO vacancyNotActiveDTOBadRequest;
    private Long vacancyToDesactivateId;
    private Vacancy vacancyToDesactivateById;
    private Long vacancyAlreadyActivatedToDesactivateId;

    @BeforeEach
    void setUp() {
        Pageable pageable = PageRequest.of(0, 10);

        vacancyDTO = new VacancyDTO();
        vacancyDTO.setId(1L);
        vacancyDTO.setRole("Software Developer");

        LanguageDetailDTO languageDetailDTO = new LanguageDetailDTO();
        languageDetailDTO.setName("English");
        languageDetailDTO.setLanguageLevel("Advanced");
        vacancyDTO.setLanguage(languageDetailDTO);

        List<VacancyDTO> vacancyDTOList = Collections.singletonList(vacancyDTO);
        vacancyPage = new PageImpl<>(vacancyDTOList, pageable, vacancyDTOList.size());

        vacancyNotActiveDTO=new VacancyNotActiveDTO();
        vacancyNotActiveDTO.setActive(false);
        vacancyNotActiveDTO.setId(1L);

        vacancyToDesactivateById=new Vacancy();
        vacancyToDesactivateById.setId(1L);
        vacancyToDesactivateById.setActive(true);
        vacancyNotActiveDTOBadRequest=new VacancyNotActiveDTO();
        vacancyNotActiveDTOBadRequest.setId(3l);
        vacancyNotActiveDTOBadRequest.setActive(true);
    }

    @Test
    void disableVacancy() throws Exception {
        vacancyToDesactivateId = 1L;
        when(vacancyService.disableVacancy(vacancyToDesactivateId)).thenReturn(vacancyNotActiveDTO);
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/portal/vacancies/disable/{vacancyToDesactivateId}", vacancyToDesactivateId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void disableVacancyAlreadyActivated() throws Exception {
        vacancyAlreadyActivatedToDesactivateId = 3L;
        when(vacancyService.disableVacancy(vacancyAlreadyActivatedToDesactivateId))
                .thenThrow(new IllegalArgumentException("The vacancy its already desactivated."));

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/portal/vacancies/disable/{vacancyId}", vacancyAlreadyActivatedToDesactivateId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void disableVacancyWithoutExist() throws Exception {
        Long id=7L;
        when(vacancyService.disableVacancy(id))
                .thenThrow(new EntityNotFoundException("Vacancy not found."));

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/portal/vacancies/disable/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    @Test
    void getAllVacanciesActive_Success() throws Exception {
        when(vacancyService.getVacanciesActiveWithLanguage(any(Pageable.class))).thenReturn(vacancyPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/portal/vacancies/public/actives")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].role").value("Software Developer"))
                .andExpect(jsonPath("$.content[0].language.language_name").value("English"))
                .andExpect(jsonPath("$.content[0].language.language_level").value("Advanced"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }



    @Test
    void getAllVacanciesActive_ServiceThrowsException() throws Exception {
        when(vacancyService.getVacanciesActiveWithLanguage(any(Pageable.class)))
                .thenThrow(new EntityNotFoundException("No active vacancies found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/portal/vacancies/public/actives")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

       @Test
    void getDetailsVacancy_Success() throws Exception{
       Long id = 1L;
       VacancyDetailsDTO mockDTO = new VacancyDetailsDTO();
       mockDTO.setSkills(List.of(new ResponseDetailSkillDTO()));

       when(vacancyService.getDetailsVacancyById(id)).thenReturn(mockDTO);

       mockMvc.perform(MockMvcRequestBuilders.get("/api/portal/vacancies/public/{id}/details", id)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.skills").isArray());
   }

    @Test
    void getDetailsVacancy_WhenVacancyNotFound_ReturnsNotFound() throws Exception {
        Long id = 99L;

        when(vacancyService.getDetailsVacancyById(id))
                .thenThrow(new EntityNotFoundException("Vacante test no encontrada"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/portal/vacancies/public/{id}/details", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void getDetailsVacancy_WhenVacancyIsInactive_ReturnsGoneStatus() throws Exception {
        Long id = 2L;

        when(vacancyService.getDetailsVacancyById(id))
                .thenThrow(new VacancyNotActiveException("Vacante inactiva"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/portal/vacancies/public/{id}/details", id))
                .andExpect(status().isGone());
    }
    @Test
    void createVacancy_Success() throws Exception {
        RequestFullVacancyDTO request = new RequestFullVacancyDTO();
        request.setIdCompany(1L);
        request.setIdRecruiter(1L);
        request.setRole("Backend Developer");
        request.setDescription("Descripción de la vacante");
        request.setActive(true);
        request.setYearsExperienceRequired(2);
        request.setExpirationDate(null);
        request.setDetailsSkills(Collections.emptyList());

        VacancyDetailsDTO response = new VacancyDetailsDTO();
        response.setId(1L);
        response.setRole("Backend Developer");

        when(vacancyService.create(any(RequestFullVacancyDTO.class))).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/portal/vacancies/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.role").value("Backend Developer"));
    }
    @Test
    void createVacancy_Unauthorized_ThrowsException() throws Exception {
        RequestFullVacancyDTO request = new RequestFullVacancyDTO();
        request.setIdCompany(1L);
        request.setIdRecruiter(1L);

        when(vacancyService.create(any(RequestFullVacancyDTO.class)))
                .thenThrow(new UnauthorizedActionException("user.without_permissions"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/portal/vacancies/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }


    @Test
    void updateVacancyShouldReturnStatus2xx() throws Exception {
        VacancyRequestUpdateDTO requestUpdateDTO = new VacancyRequestUpdateDTO();
        requestUpdateDTO.setRole("Backend Developer");
        requestUpdateDTO.setDescription("Java backend position");
        requestUpdateDTO.setYearsExperienceRequired(3);
        requestUpdateDTO.setLanguageLevel("C1");

        VacancyDTO responseDTO = new VacancyDTO();
        responseDTO.setId(1L);
        responseDTO.setRole("Backend Developer");

        LanguageDetailDTO languageDetailDTO = new LanguageDetailDTO();
        languageDetailDTO.setLanguageLevel("C1");
        responseDTO.setLanguage(languageDetailDTO);

        when(vacancyService.updateVacancy(any(), eq(1L)))
                .thenReturn(responseDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/portal/vacancies/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUpdateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("Backend Developer"))
                .andExpect(jsonPath("$.language.language_level").value("C1"));
    }

}

