package com.techforb.apiportalrecruiting.core.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techforb.apiportalrecruiting.core.dtos.contactTypes.RequestContactTypeDTO;
import com.techforb.apiportalrecruiting.core.dtos.contactTypes.ResponseContactTypeDTO;
import com.techforb.apiportalrecruiting.core.security.jwt.JwtFilter;
import com.techforb.apiportalrecruiting.core.security.jwt.JwtService;
import com.techforb.apiportalrecruiting.core.services.ContactTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContactTypeController.class)
@AutoConfigureMockMvc(addFilters = false)
class ContactTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ContactTypeService contactTypeService;
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtFilter jwtFilter;

    private ResponseContactTypeDTO responseContactTypeDTO;
    private RequestContactTypeDTO requestContactTypeDTO;

    @BeforeEach
    void setUp(){
        responseContactTypeDTO=new ResponseContactTypeDTO(1L,"Juan@example");
        requestContactTypeDTO=new RequestContactTypeDTO("Juan@example");
    }


    @Test
    void getAllContactTypes() throws Exception{
        List<ResponseContactTypeDTO>responseContactTypeDTOList=new ArrayList<>();
        responseContactTypeDTOList.add(responseContactTypeDTO);

        given(contactTypeService.getAllContactTypes()).willReturn(responseContactTypeDTOList);

        this.mockMvc.perform(get("/api/contact-type/list").contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseContactTypeDTOList)));
        verify(contactTypeService).getAllContactTypes();
    }

    @Test
    void getContactTypeById() throws Exception{
        given(contactTypeService.getContactTypeById(1L)).willReturn(responseContactTypeDTO);

        this.mockMvc.perform(get("/api/contact-type?id="+1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseContactTypeDTO)));
        verify(contactTypeService).getContactTypeById(1L);
    }

    @Test
    void create() throws Exception{
        given(contactTypeService.createContactType(requestContactTypeDTO)).willReturn(responseContactTypeDTO);

        this.mockMvc.perform(post("/api/contact-type/create")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestContactTypeDTO)))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseContactTypeDTO)));
        verify(contactTypeService).createContactType(requestContactTypeDTO);
    }

    @Test
    void update() throws Exception{
        requestContactTypeDTO.setName("Julio@example");
        responseContactTypeDTO.setName("Julio@example");
        given(contactTypeService.updateContactType(1L,requestContactTypeDTO)).willReturn(responseContactTypeDTO);

        this.mockMvc.perform(put("/api/contact-type/"+1L)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestContactTypeDTO)))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseContactTypeDTO)));
        verify(contactTypeService).updateContactType(1L,requestContactTypeDTO);
    }

    @Test
    void deleteContactType() throws Exception{
        doNothing().when(contactTypeService).deleteContactType(1L);

        this.mockMvc.perform(delete("/api/contact-type/"+1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        verify(contactTypeService).deleteContactType(1L);
    }
}