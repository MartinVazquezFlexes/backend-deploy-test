package com.techforb.apiportalrecruiting.modules.portal.person.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techforb.apiportalrecruiting.core.security.jwt.JwtFilter;
import com.techforb.apiportalrecruiting.core.security.jwt.JwtService;
import com.techforb.apiportalrecruiting.modules.portal.applications.controllers.PersonController;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.PersonService;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.person.PersonRequestDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.person.PersonResponseDTO;
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
                .country("New Country")
                .dateBirth(LocalDate.of(1990, 1, 1))
                .build();

        personResponseDTO = PersonResponseDTO.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .country("New Country")
                .dateBirth(LocalDateTime.now())
                .skillDTO(Collections.emptyList())
                .cvDTO(Collections.emptyList())
                .contactDTOS(Collections.emptyList())
                .identificationDTO(Collections.emptyList())
                .build();
    }

}
