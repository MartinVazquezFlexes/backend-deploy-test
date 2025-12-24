package com.techforb.apiportalrecruiting.core.services.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.entities.Company;
import com.techforb.apiportalrecruiting.core.repositories.CompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
@ActiveProfiles("test")
@SpringBootTest
class CompanyServiceImplTest {

    @MockitoSpyBean
    private CompanyServiceImpl companyServiceImp;
    @MockitoBean
    private CompanyRepository companyRepository;
    @MockitoBean
    private LocalizedMessageService localizedMessageService;

    private Company company;

    @BeforeEach
    void setUp(){
        company=new Company(1L,"CompanyInc");
    }

    @Test
    void findById_Success() {
        Mockito.when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        Company response=this.companyServiceImp.findById(1L);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1L,response.getId());
        Assertions.assertEquals("CompanyInc",response.getName());
    }
    @Test
    void findById_Null(){
        Mockito.when(companyRepository.findById(1L)).thenReturn(Optional.empty());
        Mockito.when(localizedMessageService.getMessage("company.not_found_by_id")).thenReturn("company.not_found_by_id");
        EntityNotFoundException ex=assertThrows(EntityNotFoundException.class,
                ()->this.companyServiceImp.findById(1L));
        Assertions.assertEquals(ex.getMessage(),"company.not_found_by_id");
    }
}