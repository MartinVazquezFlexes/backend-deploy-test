package com.techforb.apiportalrecruiting.modules.portal.applications.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techforb.apiportalrecruiting.core.entities.ApplicationState;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application.ApplicationDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application.ApplicationModified;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application.ApplicationStateUpdateDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.ApplicationEditorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ApplicationEditorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ApplicationEditorService applicationService;

    private final String apiUrl = "/api/portal/applications/";

    @Test
    void getApplicationsByUserId_ShouldReturnApplications() throws Exception {
        ApplicationDTO applicationDTO = ApplicationDTO.builder()
                .id(1L)
                .applicationState(ApplicationState.IN_PROCESS)
                .build();

        when(applicationService.getApplicationByApplicantId(anyLong()))
                .thenReturn(List.of(applicationDTO));

        mockMvc.perform(get(apiUrl + "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].applicationState").value("IN_PROCESS"));
    }

    @Test
    void updateApplication_ShouldReturnUpdatedApplication() throws Exception {
        ApplicationModified modified = new ApplicationModified();
        modified.setComments("New comment");
        modified.setApplicationState(ApplicationState.MODIFIED);

        ApplicationDTO expectedResponse = ApplicationDTO.builder()
                .id(1L)
                .comments("New comment")
                .applicationState(ApplicationState.MODIFIED)
                .build();

        when(applicationService.modifyApplication(anyLong(), any(ApplicationModified.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(put(apiUrl + "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modified)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.comments").value("New comment"))
                .andExpect(jsonPath("$.applicationState").value("MODIFIED"));
    }

	@Test
	void updateState_ShouldReturnUpdatedApplication() throws Exception {
		ApplicationStateUpdateDTO stateUpdate = new ApplicationStateUpdateDTO();
		stateUpdate.setApplicationState(ApplicationState.CANCELED);

		ApplicationDTO expectedResponse = ApplicationDTO.builder()
				.id(1L)
				.applicationState(ApplicationState.CANCELED)
				.build();

		when(applicationService.modifyStateApplication(1L, ApplicationState.CANCELED))
				.thenReturn(expectedResponse);

		mockMvc.perform(put(apiUrl +"1/modify")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(stateUpdate)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.applicationState").value("CANCELED"));
	}
}