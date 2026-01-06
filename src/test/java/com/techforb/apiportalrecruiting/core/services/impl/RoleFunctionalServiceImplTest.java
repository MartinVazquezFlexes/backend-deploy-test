package com.techforb.apiportalrecruiting.core.services.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.entities.RoleFunctional;
import com.techforb.apiportalrecruiting.core.repositories.RoleFunctionalRepository;
import com.techforb.apiportalrecruiting.core.entities.Person;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.core.repositories.UserRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.PersonRepository;
import com.techforb.apiportalrecruiting.modules.portal.services.impl.RoleFunctionalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleFunctionalServiceImplTest {
    @Mock
    private RoleFunctionalRepository roleFunctionalRepository;
    @InjectMocks
    private RoleFunctionalServiceImpl roleFunctionalService;
    @Mock
    private LocalizedMessageService localizedMessageService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PersonRepository personRepository;
    List<RoleFunctional> roleFunctionalList;
    RoleFunctional roleFunctional;
    Person person;
    UserEntity user;

    @BeforeEach
    void setUp() {
    roleFunctionalList=new ArrayList<>();
    roleFunctional=new RoleFunctional();
    roleFunctional.setId(1L);
    roleFunctional.setName("Backend Developer");
    roleFunctionalList.add(roleFunctional);
    user = UserEntity.builder().id(5L).email("test@techforb.com").build();
    person = Person.builder().id(9L).user(user).build();
    user.setPerson(person);
    }

    @Test
    void getAllRolesFunctional() {
        when(roleFunctionalRepository.findAll()).thenReturn(roleFunctionalList);
        List<RoleFunctional>response=roleFunctionalService.getAllRolesFunctional();
        assertNotNull(response);
        assertEquals(1,response.size());
        assertEquals("Backend Developer",response.get(0).getName());
        verify(roleFunctionalRepository).findAll();
    }

    @Test
    void getAllRolesFunctional_EmptyList() {
        when(roleFunctionalRepository.findAll()).thenReturn(new ArrayList<>());
        List<RoleFunctional>response=roleFunctionalService.getAllRolesFunctional();
        assertNotNull(response);
        assertTrue(response.isEmpty());
        verify(roleFunctionalRepository).findAll();
    }
    @Test
    void getAllRolesFunctional_ShouldThrowRuntimeException_WhenDataAccessExceptionOccurs() {
        when(roleFunctionalRepository.findAll())
                .thenThrow(new DataAccessException("DB error") {});

        when(localizedMessageService.getMessage("error.fetching.functional.roles"))
                .thenReturn("error.fetching.functional.roles");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> roleFunctionalService.getAllRolesFunctional());

        assertTrue(ex.getMessage().contains("error.fetching.functional.roles"));
        verify(roleFunctionalRepository).findAll();
    }

    @Test
    void saveRoleFunctional() {
        String email = "test@techforb.com";
        Long roleId = 1L;

        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));
        when(roleFunctionalRepository.findById(roleId)).thenReturn(java.util.Optional.of(roleFunctional));

        RoleFunctional result = roleFunctionalService.saveRoleFunctional(roleId, email);

        assertNotNull(result);
        assertEquals(roleId, result.getId());
        assertNotNull(person.getRoleFunctionals());
        assertEquals(1, person.getRoleFunctionals().size());
        assertEquals(roleId, person.getRoleFunctionals().get(0).getId());
        verify(personRepository).save(person);
    }

    @Test
    void saveRoleFunctional_ShouldReplaceExistingRoles() {
        String email = "test@techforb.com";
        Long roleId = 1L;

        // existing different role
        RoleFunctional existing = new RoleFunctional();
        existing.setId(99L);
        existing.setName("Old Role");
        person.setRoleFunctionals(new ArrayList<>(List.of(existing)));

        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));
        when(roleFunctionalRepository.findById(roleId)).thenReturn(java.util.Optional.of(roleFunctional));

        roleFunctionalService.saveRoleFunctional(roleId, email);

        assertNotNull(person.getRoleFunctionals());
        assertEquals(1, person.getRoleFunctionals().size());
        assertEquals(roleId, person.getRoleFunctionals().get(0).getId());
        verify(personRepository).save(person);
    }

    @Test
    void saveRoleFunctional_ShouldThrow_WhenUserNotFound() {
        String email = "missing@techforb.com";
        Long roleId = 1L;

        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.empty());
        when(localizedMessageService.getMessage("user.not_found")).thenReturn("user.not_found");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> roleFunctionalService.saveRoleFunctional(roleId, email));
        assertTrue(ex.getMessage().contains("user.not_found"));
    }

    @Test
    void saveRoleFunctional_ShouldThrow_WhenPersonIsNull() {
        String email = "test@techforb.com";
        Long roleId = 1L;

        UserEntity onlyUser = UserEntity.builder().id(2L).email(email).build();
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(onlyUser));
        when(localizedMessageService.getMessage("person.not_found")).thenReturn("person.not_found");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> roleFunctionalService.saveRoleFunctional(roleId, email));
        assertTrue(ex.getMessage().contains("person.not_found"));
    }

    @Test
    void saveRoleFunctional_ShouldThrow_WhenRoleNotFound() {
        String email = "test@techforb.com";
        Long roleId = 99L;

        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));
        when(roleFunctionalRepository.findById(roleId)).thenReturn(java.util.Optional.empty());
        when(localizedMessageService.getMessage("functional.role.not_found", roleId))
                .thenReturn("functional.role.not_found");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> roleFunctionalService.saveRoleFunctional(roleId, email));
        assertTrue(ex.getMessage().contains("functional.role.not_found"));
    }

    @Test
    void saveRoleFunctional_ShouldThrow_WhenDataAccessExceptionOnSave() {
        String email = "test@techforb.com";
        Long roleId = 1L;

        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));
        when(roleFunctionalRepository.findById(roleId)).thenReturn(java.util.Optional.of(roleFunctional));
        when(localizedMessageService.getMessage("error.saving.functional.role"))
                .thenReturn("error.saving.functional.role");

        when(personRepository.save(person)).thenThrow(new DataAccessException("DB error") {});

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> roleFunctionalService.saveRoleFunctional(roleId, email));
        assertTrue(ex.getMessage().contains("error.saving.functional.role"));
    }
}