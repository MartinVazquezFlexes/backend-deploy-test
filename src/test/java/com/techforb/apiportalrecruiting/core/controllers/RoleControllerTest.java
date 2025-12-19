package com.techforb.apiportalrecruiting.core.controllers;

import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.core.services.RoleService;
import com.techforb.apiportalrecruiting.modules.backoffice.user.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RoleControllerTest {
    @Mock
    private RoleService roleService;

    @Mock
    private UserService userService;

    @InjectMocks
    private RoleController roleController;

    RoleControllerTest() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void selfAssignRole() {
        String roleName = "ADMIN";
        String email = "user@test.com";

        UserEntity mockUser = new UserEntity();
        mockUser.setEmail(email);

        when(userService.getUserFromContext()).thenReturn(mockUser);

        ResponseEntity<String> response = roleController.selfAssignRole(roleName);

        verify(roleService, times(1)).assignRoleToUser(roleName, email);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Rol asignado exitosamente");
    }
}