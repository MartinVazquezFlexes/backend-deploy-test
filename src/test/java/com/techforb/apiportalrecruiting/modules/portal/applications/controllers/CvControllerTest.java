package com.techforb.apiportalrecruiting.modules.portal.applications.controllers;

import com.techforb.apiportalrecruiting.core.entities.Person;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.cv.CvWithCreationDateDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.cv.ResponsePagCvDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.CvService;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@EnableSpringDataWebSupport(
        pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO
)
class CvControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CvService cvService;

    @MockitoBean
    private PersonService personService;

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
    @Test
    void getFilteredCvs_withCountryAndSkill_shouldReturnOk() throws Exception {

        Page<ResponsePagCvDTO> page =
                new PageImpl<>(new ArrayList<>(), PageRequest.of(0, 10), 0);

        when(cvService.getFilteredCvs(
                eq("Argentina"), eq("Java"), any(Pageable.class)
        )).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/cv/get-cvs-filtered")
                        .param("country", "Argentina")
                        .param("skill", "Java")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk());

        verify(cvService).getFilteredCvs(eq("Argentina"), eq("Java"), any(Pageable.class));
    }
    @Test
    void getCvsById_ReturnOk() throws Exception {
        Long personId = 1L;
        Boolean isLast = true;

        Page<CvWithCreationDateDTO> page =
                new PageImpl<>(new ArrayList<>(), PageRequest.of(0, 10), 0);

        when(cvService.getCvsById(
                eq(personId),
                eq(isLast),
                any(Pageable.class)
        )).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/cv/get-myCvs/{idPerson}", personId)
                        .param("isLast", String.valueOf(isLast))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        verify(cvService).getCvsById(
                eq(personId),
                eq(isLast),
                any(Pageable.class)
        );
    }
    @Test
    void uploadCv_ReturnCreated() throws Exception {
        Long personId = 1L;

        MockMultipartFile cvFile = new MockMultipartFile(
                "cv",
                "cv.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "dummy pdf content".getBytes()
        );

        MockMultipartFile fromProfile = new MockMultipartFile(
                "fromProfile",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                "true".getBytes()
        );

        Person person = new Person();
        person.setId(personId);

        when(personService.getPersonById(personId)).thenReturn(person);
        when(cvService.uploadCv(any(), eq(person), eq(""), eq(true)))
                .thenReturn(null);

        mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/api/cv/upload/{idPerson}", personId)
                                .file(cvFile)
                                .file(fromProfile)   // 👈 CLAVE
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string("Cv upload successful"));

        verify(personService).getPersonById(personId);
        verify(cvService).uploadCv(any(), eq(person), eq(""), eq(true));
    }

}