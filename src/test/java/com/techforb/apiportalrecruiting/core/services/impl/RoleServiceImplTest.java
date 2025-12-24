package com.techforb.apiportalrecruiting.core.services.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.entities.Role;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.core.repositories.RoleRepository;
import com.techforb.apiportalrecruiting.modules.backoffice.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private LocalizedMessageService localizedMessageService;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private final  String roleDefault = "ROLE_DEFAULT";

    private final  String roleAdmin = "ROLE_ADMIN";

    private  UserEntity userEntity;

    private Role role;

    @BeforeEach
    void setUp() {
    userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail("test@gmail.com");
        role = new Role();
        role.setId(1L);
        role.setName(roleAdmin);
        userEntity.setRoles(new ArrayList<>());
    }


    @Test
    void assignRoleToUserWithThrows() {
        when(userRepository.findByEmail(userEntity.getEmail())).thenReturn(Optional.empty());
        when(localizedMessageService.getMessage("user.not_found"))
                .thenReturn("User not found");
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roleService.assignRoleToUser(roleAdmin, userEntity.getEmail());
        });
        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findByEmail(userEntity.getEmail());
    }

    @Test
    void assignRoleToUserWithRoleNotFoundThrows() {
        when(userRepository.findByEmail(userEntity.getEmail())).thenReturn(Optional.of(userEntity));
        when(roleRepository.findByName(roleAdmin)).thenReturn(Optional.empty());
        when(localizedMessageService.getMessage("role.not_found"))
                .thenReturn("Role not found");
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roleService.assignRoleToUser(roleAdmin, userEntity.getEmail());
        });
        assertEquals("Role not found", exception.getMessage());
        verify(userRepository).findByEmail(userEntity.getEmail());
        verify(roleRepository).findByName(roleAdmin);
    }

    @Test
    void assignRoleToUserWithoutThrows() {
        when(userRepository.findByEmail(userEntity.getEmail())).thenReturn(Optional.of(userEntity));
        when(roleRepository.findByName(roleAdmin)).thenReturn(Optional.of(role));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        roleService.assignRoleToUser(roleAdmin, userEntity.getEmail());
        assertNotNull(userEntity.getRoles());
        assertFalse(userEntity.getRoles().isEmpty());
        assertEquals(1, userEntity.getRoles().size());
        assertEquals(roleAdmin, userEntity.getRoles().get(0).getName());
        verify(userRepository).save(userEntity);
        verify(userRepository).findByEmail(userEntity.getEmail());
        verify(roleRepository).findByName(roleAdmin);
    }

    @Test
    void findByNameWithOutThrow() {
        Role role = new Role();
        role.setName(roleDefault);

        when(roleRepository.findByName(roleDefault)).thenReturn(Optional.of(role));

        Role foundRole = roleService.findByName(roleDefault);

        assertNotNull(foundRole);
        assertEquals(roleDefault, foundRole.getName());
    }

    @Test
    void findByNameWithThrow() {
        when(roleRepository.findByName(roleAdmin)).thenReturn(Optional.empty());
        when(localizedMessageService.getMessage("role.not_found"))
                .thenReturn("Role not found");
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roleService.findByName(roleAdmin);
        });
        assertEquals("Role not found", exception.getMessage());
    }




}