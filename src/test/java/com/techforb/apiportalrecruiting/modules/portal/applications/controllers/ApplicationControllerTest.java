package com.techforb.apiportalrecruiting.modules.portal.applications.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application.RequestApplicationDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application.RequestChangeCvApplicationDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application.ResponseApplicationDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ApplicationService applicationService;

    private RequestApplicationDTO requestApplicationDTO;
    private MockMultipartFile cvFile;
    private MockMultipartFile applicationJson;

    private final String API_URL = "/api/portal/applications/";

	private ResponseApplicationDTO responseApplicationDTO;

    @BeforeEach
    void setUp() throws Exception {
        requestApplicationDTO = new RequestApplicationDTO();
		responseApplicationDTO = new ResponseApplicationDTO();

        cvFile = new MockMultipartFile(
                "cv", "cv.pdf", MediaType.APPLICATION_PDF_VALUE, "dummy data".getBytes()
        );

        applicationJson = new MockMultipartFile(
                "application", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(requestApplicationDTO)
        );
    }

    @Test
    void testApplyVacancy_Success() throws Exception {
		when(applicationService.applyVacancy(any(RequestApplicationDTO.class), any(MockMultipartFile.class)))
				.thenReturn(responseApplicationDTO);
        mockMvc.perform(MockMvcRequestBuilders.multipart(API_URL + "apply-vacancy")
                        .file(applicationJson)
                        .file(cvFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    @Test
    void testApplyVacancy_InvalidRequest_ShouldReturnInternalServerError() throws Exception {
		when(applicationService.applyVacancy(any(RequestApplicationDTO.class), any(MockMultipartFile.class)))
				.thenReturn(responseApplicationDTO);
        mockMvc.perform(MockMvcRequestBuilders.multipart(API_URL + "apply-vacancy")
                        .file(cvFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testApplyVacancy_NotFile_ShouldReturnInternalServer() throws Exception {
		when(applicationService.applyVacancy(any(RequestApplicationDTO.class), any(MockMultipartFile.class)))
				.thenReturn(responseApplicationDTO);
        mockMvc.perform(MockMvcRequestBuilders.multipart(API_URL + "apply-vacancy")
                        .file(applicationJson))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Required part 'cv' is not present."));
    }

    @Test
    void testApplyVacancy_NotJson_ShouldReturnInternalServer() throws Exception {
		when(applicationService.applyVacancy(any(RequestApplicationDTO.class), any(MockMultipartFile.class)))
				.thenReturn(responseApplicationDTO);
        mockMvc.perform(MockMvcRequestBuilders.multipart(API_URL + "apply-vacancy")
                        .file(cvFile))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Required part 'application' is not present."));
    }


    @Test
    void testApplyVacancy_ShouldCallServiceWithCorrectParameters() throws Exception {
		when(applicationService.applyVacancy(any(RequestApplicationDTO.class), any(MockMultipartFile.class)))
				.thenReturn(responseApplicationDTO);
        mockMvc.perform(MockMvcRequestBuilders.multipart(API_URL + "apply-vacancy")
                        .file(applicationJson)
                        .file(cvFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

	@Test
	void testChangeCvApplication_Success() throws Exception {
		RequestChangeCvApplicationDTO request = new RequestChangeCvApplicationDTO();
		request.setApplicationId(1L);
		request.setCvId(2L);

		when(applicationService.changeCvApplication(any(RequestChangeCvApplicationDTO.class)))
				.thenReturn(responseApplicationDTO);

		mockMvc.perform(put(API_URL + "modify-application-vacancy")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk());
	}

	@Test
	void testChangeCvApplication_ShouldReturnInternalServerError_WhenServiceFails() throws Exception {
		RequestChangeCvApplicationDTO request = new RequestChangeCvApplicationDTO();
		request.setApplicationId(1L);
		request.setCvId(2L);

		when(applicationService.changeCvApplication(any(RequestChangeCvApplicationDTO.class)))
				.thenThrow(new RuntimeException("Unexpected error"));

		mockMvc.perform(put(API_URL + "modify-application-vacancy")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isInternalServerError());
	}
}