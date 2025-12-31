package com.techforb.apiportalrecruiting.core.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techforb.apiportalrecruiting.core.dtos.contacts.RequestContactDTO;
import com.techforb.apiportalrecruiting.core.dtos.contacts.ResponseContactDTO;
import com.techforb.apiportalrecruiting.core.services.ContactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContactService contactService;

    @Autowired
    private ObjectMapper objectMapper;

    private RequestContactDTO requestContactDTO;
    private ResponseContactDTO responseContactDTO;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        requestContactDTO = RequestContactDTO.builder()
                .contactTypeId(1L)
                .value("contact@example.com")
                .label("Personal")
                .build();

        responseContactDTO = ResponseContactDTO.builder()
                .id(1L)
                .contactType("Email")
                .value("contact@example.com")
                .label("Personal")
                .fullName("John Doe")
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Test
    void getAllContactsByPerson_Success() throws Exception {
        List<ResponseContactDTO> contacts = Collections.singletonList(responseContactDTO);
        when(contactService.getContactsByPersonId()).thenReturn(contacts);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/contact/list")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].contactType").value("Email"))
                .andExpect(jsonPath("$[0].value").value("contact@example.com"))
                .andExpect(jsonPath("$[0].label").value("Personal"))
                .andExpect(jsonPath("$[0].fullName").value("John Doe"));
    }

    @Test
    void getAllContactsByPerson_EmptyList() throws Exception {
        when(contactService.getContactsByPersonId()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/contact/list")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getById_Success() throws Exception {
        Long id = 1L;
        when(contactService.getContactById(id)).thenReturn(responseContactDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/contact/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.contactType").value("Email"))
                .andExpect(jsonPath("$.value").value("contact@example.com"))
                .andExpect(jsonPath("$.fullName").value("John Doe"));
    }

    @Test
    void getById_NotFound() throws Exception {
        Long id = 99L;
        when(contactService.getContactById(id))
                .thenThrow(new jakarta.persistence.EntityNotFoundException("Contact not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/contact/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_Success() throws Exception {
        when(contactService.createContact(any(RequestContactDTO.class))).thenReturn(responseContactDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/contact/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestContactDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.value").value("contact@example.com"))
                .andExpect(jsonPath("$.contactType").value("Email"))
                .andExpect(jsonPath("$.label").value("Personal"));
    }

    @Test
    void create_PhoneContact_Success() throws Exception {
        RequestContactDTO phoneRequest = RequestContactDTO.builder()
                .contactTypeId(2L)
                .value("+5493512345678")
                .label("Mobile")
                .build();

        ResponseContactDTO phoneResponse = ResponseContactDTO.builder()
                .id(2L)
                .contactType("Phone")
                .value("+5493512345678")
                .label("Mobile")
                .fullName("John Doe")
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(contactService.createContact(any(RequestContactDTO.class))).thenReturn(phoneResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/contact/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(phoneRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.contactType").value("Phone"))
                .andExpect(jsonPath("$.value").value("+5493512345678"))
                .andExpect(jsonPath("$.label").value("Mobile"));
    }

    @Test
    void update_Success() throws Exception {
        Long id = 1L;
        RequestContactDTO updateRequest = RequestContactDTO.builder()
                .contactTypeId(1L)
                .value("newemail@example.com")
                .label("Work")
                .build();

        ResponseContactDTO updatedResponse = ResponseContactDTO.builder()
                .id(id)
                .contactType("Email")
                .value("newemail@example.com")
                .label("Work")
                .fullName("John Doe")
                .createdAt(now)
                .updatedAt(LocalDateTime.now())
                .build();

        when(contactService.updateContact(eq(id), any(RequestContactDTO.class))).thenReturn(updatedResponse);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/contact/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.value").value("newemail@example.com"))
                .andExpect(jsonPath("$.label").value("Work"));
    }

    @Test
    void update_NotFound() throws Exception {
        Long id = 99L;
        when(contactService.updateContact(eq(id), any(RequestContactDTO.class)))
                .thenThrow(new jakarta.persistence.EntityNotFoundException("Contact not found"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/contact/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestContactDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_Success() throws Exception {
        Long id = 1L;
        doNothing().when(contactService).deleteContactById(id);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/contact/{id}", id))
                .andExpect(status().isNoContent());

        verify(contactService, times(1)).deleteContactById(id);
    }

    @Test
    void delete_NotFound() throws Exception {
        Long id = 99L;
        doThrow(new jakarta.persistence.EntityNotFoundException("Contact not found"))
                .when(contactService).deleteContactById(id);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/contact/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllContactsByPerson_MultipleContacts() throws Exception {
        ResponseContactDTO emailContact = ResponseContactDTO.builder()
                .id(1L)
                .contactType("Email")
                .value("contact@example.com")
                .label("Personal")
                .fullName("John Doe")
                .createdAt(now)
                .updatedAt(now)
                .build();

        ResponseContactDTO phoneContact = ResponseContactDTO.builder()
                .id(2L)
                .contactType("Phone")
                .value("+5493512345678")
                .label("Mobile")
                .fullName("John Doe")
                .createdAt(now)
                .updatedAt(now)
                .build();

        List<ResponseContactDTO> contacts = List.of(emailContact, phoneContact);
        when(contactService.getContactsByPersonId()).thenReturn(contacts);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/contact/list")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].contactType").value("Email"))
                .andExpect(jsonPath("$[1].contactType").value("Phone"));
    }
}