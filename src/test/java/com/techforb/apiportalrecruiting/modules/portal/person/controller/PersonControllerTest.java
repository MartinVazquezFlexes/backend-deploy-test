package com.techforb.apiportalrecruiting.modules.portal.person.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techforb.apiportalrecruiting.core.security.jwt.JwtFilter;
import com.techforb.apiportalrecruiting.core.security.jwt.JwtService;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.PersonService;
import com.techforb.apiportalrecruiting.modules.portal.person.dto.PersonRequestDTO;
import com.techforb.apiportalrecruiting.modules.portal.person.dto.PersonResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PersonController.class)
@AutoConfigureMockMvc(addFilters = false)
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonService personService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private JwtService jwtService;

    private Long personId;
    private PersonRequestDTO personRequestDTO;
    private PersonResponseDTO personResponseDTO;

    @BeforeEach
    void setUp() {
        personId = 1L;

        personRequestDTO = PersonRequestDTO.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .directionId(123L)
                .directionDescription("New Address")
                .city("New City")
                .zipCode("54321")
                .province("New Province")
                .country("New Country")
                .dateBirth(LocalDate.of(1990, 1, 1))
                .skillDTO(Collections.emptyList())
                .contactDTOS(Collections.emptyList())
                .identificationDTO(Collections.emptyList())
                .build();

        personResponseDTO = PersonResponseDTO.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .directionId(123L)
                .directionDescription("New Address")
                .city("New City")
                .zipCode("54321")
                .province("New Province")
                .country("New Country")
                .dateBirth(LocalDateTime.now())
                .skillDTO(Collections.emptyList())
                .cvDTO(Collections.emptyList())
                .contactDTOS(Collections.emptyList())
                .identificationDTO(Collections.emptyList())
                .build();
    }

    @Test
    void shouldReturnPersonById() throws Exception {
        Mockito.when(personService.getPersonByIdDTO(personId)).thenReturn(personResponseDTO);

        mockMvc.perform(get("/api/portal/person/{id}", personId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"));
    }

    @Test
    void shouldUpdatePerson() throws Exception {
        Mockito.when(personService.updatePerson(Mockito.eq(personId), Mockito.any(PersonRequestDTO.class)))
                .thenReturn(personResponseDTO);

        mockMvc.perform(put("/api/portal/person/{id}", personId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jane.smith@example.com"));
    }
}
