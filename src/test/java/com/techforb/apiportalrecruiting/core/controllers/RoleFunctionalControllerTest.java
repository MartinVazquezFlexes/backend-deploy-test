package com.techforb.apiportalrecruiting.core.controllers;

import com.techforb.apiportalrecruiting.core.entities.RoleFunctional;
import com.techforb.apiportalrecruiting.core.services.RoleFunctionalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class RoleFunctionalControllerTest {
    @Mock
    private RoleFunctionalService roleFunctionalService;
    @InjectMocks
    private RoleFunctionalController roleFunctionalController;
    List<RoleFunctional> roleFunctionalList;
    RoleFunctional roleFunctional;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        roleFunctionalList=new ArrayList<>();
        roleFunctional=new RoleFunctional();
        roleFunctional.setId(1L);
        roleFunctional.setName("Backend Developer");
        roleFunctionalList.add(roleFunctional);
    }

    @Test
    void getAllRolesFunctional() {
        when(roleFunctionalService.getAllRolesFunctional()).thenReturn(roleFunctionalList);

        ResponseEntity<List<RoleFunctional>> response = roleFunctionalController.getAllRolesFunctional();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Backend Developer", response.getBody().get(0).getName());
    }
}