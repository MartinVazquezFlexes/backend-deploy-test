package com.techforb.apiportalrecruiting.modules.portal.applications.controllers;

import com.techforb.apiportalrecruiting.modules.portal.applications.services.CvService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class CvControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CvService cvService;

    @BeforeEach
    void setUp() {
        reset(cvService);
    }

    @Test
    void deleteCv() throws Exception {
        Long personId = 1L;
        Long cvId = 1L;

        when(cvService.deleteCvByIdAndPersonId(cvId, personId)).thenReturn(true);

        mockMvc.perform(delete("/api/cv/delete")
                .param("personId", String.valueOf(personId))
                .param("cvId", String.valueOf(cvId))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(cvService).deleteCvByIdAndPersonId(cvId, personId);
    }
}