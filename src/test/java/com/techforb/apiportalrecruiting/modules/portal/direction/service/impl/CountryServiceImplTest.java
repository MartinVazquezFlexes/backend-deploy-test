package com.techforb.apiportalrecruiting.modules.portal.direction.service.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.dtos.CountrySavedDTO;
import com.techforb.apiportalrecruiting.core.entities.Country;
import com.techforb.apiportalrecruiting.core.entities.Person;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.modules.backoffice.user.UserRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.PersonRepository;
import com.techforb.apiportalrecruiting.modules.portal.direction.dto.CountryItemDTO;
import com.techforb.apiportalrecruiting.modules.portal.direction.repository.CountryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryServiceImplTest {

    @Mock
    private CountryRepository countryRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CountryServiceImpl countryService;
    @Mock
    private LocalizedMessageService localizedMessageService;
    @Mock
    private PersonRepository personRepository;


    @Test
    void listAll_shouldReturnIdsAndNames_sortedByNameAsc() {
        Country c1 = new Country(); c1.setId(2L); c1.setName("Brasil");
        Country c2 = new Country(); c2.setId(1L); c2.setName("Argentina");
        when(countryRepository.findAll(any(Sort.class))).thenReturn(List.of(c1, c2));

        List<CountryItemDTO> result = countryService.listAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getId());
        assertEquals("Brasil", result.get(0).getName());
        assertEquals(1L, result.get(1).getId());
        assertEquals("Argentina", result.get(1).getName());

        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
        verify(countryRepository, times(1)).findAll(sortCaptor.capture());
        Sort sort = sortCaptor.getValue();
        assertNotNull(sort.getOrderFor("name"));
        assertTrue(sort.getOrderFor("name").isAscending());
    }

    @Test
    void saveCountry_shouldUpdateExistingCountryResidence() {
        Country old = new Country(); old.setId(10L); old.setName("Uruguay");
        Country newCountry = new Country(); newCountry.setId(1L); newCountry.setName("Argentina");

        UserEntity user = new UserEntity(); user.setId(5L);
        Person person = new Person(); person.setId(1L); person.setUser(user);
        person.setCountryResidence(old);
        user.setPerson(person); user.setEmail("test@test.com");

        when(userRepository.findByEmail("test@test.com")).thenReturn(java.util.Optional.of(user));
        when(countryRepository.findById(1L)).thenReturn(java.util.Optional.of(newCountry));
        when(personRepository.save(any(Person.class))).thenReturn(person);

        countryService.saveCountry(1L, "test@test.com");

        assertNotNull(person.getCountryResidence());
        assertEquals(1L, person.getCountryResidence().getId());
        assertEquals("Argentina", person.getCountryResidence().getName());
        verify(personRepository, times(1)).save(any(Person.class));
    }

    @Test
    void saveCountry() {
        Country c2 = new Country(); c2.setId(1L); c2.setName("Argentina");
        UserEntity user = new UserEntity(); user.setId(5L);
        Person person= new Person();
        person.setId(1L);
        person.setUser(user);
        user.setPerson(person);
        user.setEmail("test@test.com");
        when(userRepository.findByEmail("test@test.com")).thenReturn(java.util.Optional.of(user));
        when(countryRepository.findById(1L)).thenReturn(java.util.Optional.of(c2));
        when(personRepository.save(any(Person.class))).thenReturn(person);
        CountrySavedDTO result = countryService.saveCountry(1L, "test@test.com");
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Argentina", result.getName());
        verify(userRepository, times(1)).findByEmail("test@test.com");
        verify(countryRepository, times(1)).findById(1L);
        verify(personRepository, times(1)).save(any(Person.class));
        verifyNoMoreInteractions(userRepository, countryRepository, personRepository);
    }

    @Test
    void saveCountry_userNotFound() {
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(java.util.Optional.empty());
        when(localizedMessageService.getMessage("user.not_found")).thenReturn("User not found.");
        Exception exception = assertThrows(RuntimeException.class, () -> {
            countryService.saveCountry(1L, "test@gmail.com");
        });
        assertEquals("User not found.", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("test@gmail.com");
        verifyNoMoreInteractions(userRepository, localizedMessageService);
    }

    @Test
    void saveCounytry_countryNotFound() {
        UserEntity user = new UserEntity();
        user.setId(5L);
        Person person = new Person();
        person.setId(1L);
        person.setUser(user);
        user.setPerson(person);
        user.setEmail("test@gmail.com");
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(java.util.Optional.of(user));
        when(countryRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        when(localizedMessageService.getMessage("country.not_found", 1L)).thenReturn("Country not found.");
        Exception exception = assertThrows(RuntimeException.class, () -> {
            countryService.saveCountry(1L, "test@gmail.com");
        });
        assertEquals("Country not found.", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("test@gmail.com");
        verify(countryRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(userRepository, countryRepository, personRepository);
    }
}
