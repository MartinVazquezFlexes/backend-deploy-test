package com.techforb.apiportalrecruiting.modules.portal.applications.services.impl;

import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.contacts.ReqResContactDTO;
import com.techforb.apiportalrecruiting.core.entities.Category;
import com.techforb.apiportalrecruiting.core.entities.City;
import com.techforb.apiportalrecruiting.core.entities.Contact;
import com.techforb.apiportalrecruiting.core.entities.ContactType;
import com.techforb.apiportalrecruiting.core.entities.Country;
import com.techforb.apiportalrecruiting.core.entities.Direction;
import com.techforb.apiportalrecruiting.core.entities.Person;
import com.techforb.apiportalrecruiting.core.entities.Province;
import com.techforb.apiportalrecruiting.core.entities.Skill;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.core.entities.ZipCode;
import com.techforb.apiportalrecruiting.core.repositories.ContactRepository;
import com.techforb.apiportalrecruiting.core.repositories.ContactTypeRepository;
import com.techforb.apiportalrecruiting.modules.portal.services.ContactTypeService;
import com.techforb.apiportalrecruiting.core.repositories.UserRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.SkillDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.PersonRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.SkillRepository;
import com.techforb.apiportalrecruiting.modules.portal.direction.repository.*;
import com.techforb.apiportalrecruiting.modules.portal.identification.dto.IdentificationDTO;
import com.techforb.apiportalrecruiting.modules.portal.identification.dto.IdentificationTypeDTO;
import com.techforb.apiportalrecruiting.modules.portal.identification.repository.IdentificationRepository;
import com.techforb.apiportalrecruiting.modules.portal.identification.repository.IdentificationTypeRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.person.PersonRequestDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.person.PersonResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
class PersonServiceImplTest {
    @MockitoBean
    private PersonRepository personRepository;
    @MockitoBean
    private DirectionRepository directionRepository;
    @MockitoBean
    private SkillRepository skillRepository;
    @MockitoBean
    private ContactRepository contactRepository;
    @MockitoBean
    private ContactTypeRepository contactTypeRepository;
    @MockitoBean
    private ContactTypeService contactTypeService;
    @MockitoBean
    private IdentificationRepository identificationRepository;
    @MockitoBean
    private IdentificationTypeRepository identificationTypeRepository;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private CountryRepository countryRepository;
    @MockitoBean
    private ProvinceRepository provinceRepository;
    @MockitoBean
    private CityRepository cityRepository;
    @MockitoBean
    private ZipCodeRepository zipCodeRepository;
    @Autowired
    private PersonServiceImpl personService;

    private Long personId = 1L;
    private Long skillId = 1L;
    private PersonRequestDTO requestDTO;
    private Person existingPerson;
    private Skill skill;
    private UserEntity user;
    private Country country;

    @BeforeEach
    void setUp() {
        country = Country.builder().id(1L).name("Argentina").build();
        user = UserEntity.builder().email("juan@example.com").build();
        skill = Skill.builder().id(skillId).description("Java").category(Category.builder().name("Backend").build()).build();

        existingPerson = Person.builder()
                .id(personId)
                .firstName("Juan")
                .lastName("Pérez")
                .dateBirth(LocalDateTime.now())
                .user(user)
                .countryResidence(country)
                .skills(new ArrayList<>())
                .contacts(new ArrayList<>())
                .identifications(new ArrayList<>())
                .cvs(new ArrayList<>())
                .build();

        user.setPerson(existingPerson);
        requestDTO = PersonRequestDTO.builder()
                .firstName("Daniel")
                .lastName("Espinoza")
                .email("juan@example.com")
                .country(country.getName())
                .dateBirth(LocalDate.of(1993, 3, 22))
                .build();

        // Mocks de repositorios
        when(personRepository.findById(personId)).thenReturn(Optional.of(existingPerson));
        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(user));
        when(personRepository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(countryRepository.findByNameIgnoreCase("Argentina")).thenReturn(Optional.of(country));
    }

    @Test
    void updatePerson_directionNotFound_throwsException() {
        when(personRepository.findById(personId)).thenReturn(Optional.of(existingPerson));
        when(directionRepository.findById(directionId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> personService.updatePerson(personId, requestDTO));
    }

    @Test
    void updatePerson_skillNotFound_throwsException() {
        when(personRepository.findById(personId)).thenReturn(Optional.of(existingPerson));
        when(directionRepository.findById(directionId)).thenReturn(Optional.of(direction));
        when(skillRepository.findById(skillId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> personService.updatePerson(personId, requestDTO));
    }

    @Test
    void updatePerson_userNotFound_throwsException() {
        when(personRepository.findById(personId)).thenReturn(Optional.of(existingPerson));
        when(directionRepository.findById(directionId)).thenReturn(Optional.of(direction));
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> personService.updatePerson(personId, requestDTO));
    }

    @Test
    void getPersonById_successfulGet_returnsPerson() {

        Person person = personService.getPersonById(personId);

        assertNotNull(person);
        assertEquals("Juan", person.getFirstName());
        assertEquals("Pérez", person.getLastName());
    }

    @Test
    void getPersonById_personNotFound_throwsException() {
        when(personRepository.findById(personId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> personService.getPersonById(personId));
    }

    @Test
    void getPersonByIdDTO_successfulGet_returnsPersonDTO() {
        PersonResponseDTO personResponseDTOTest = new PersonResponseDTO();
        personResponseDTOTest.setFirstName("Juan");
        personResponseDTOTest.setLastName("Perez");

        PersonResponseDTO personresp = personService.getPersonByIdDTO(personId);

        assertNotNull(personresp);
        assertEquals("Juan", personresp.getFirstName());
        assertEquals("Pérez", personresp.getLastName());
    }

    @Test
    void getPersonByIdDTO_personNotFound_throwsException() {
        when(personRepository.findById(personId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> personService.getPersonByIdDTO(personId));
    }

    @Test
    void createPerson_successfulCreate_returnsCreatedPerson() {
        when(personRepository.save(any(Person.class))).thenReturn(existingPerson);
        Person person = personService.createPerson(user);
        assertNotNull(person);
        assertEquals("Juan", person.getFirstName());
        assertEquals("Pérez", person.getLastName());
    }

    @Test
    void testUpdatePerson_CreatesNewContact() {
        ReqResContactDTO newContactDTO = ReqResContactDTO.builder()
                .id(null) //ID null para simular nuevo contacto
                .value("nuevo@email.com")
                .label("Email")
                .contactType("EMAIL")
                .build();

        requestDTO.setContactDTOS(List.of(newContactDTO));

        ContactType emailType = ContactType.builder().id(2L).name("EMAIL").build();
        when(contactTypeService.getContactTypeByName("EMAIL")).thenReturn(emailType);
        when(contactRepository.save(any(Contact.class))).thenAnswer(invocation -> {
            Contact c = invocation.getArgument(0);
            c.setId(99L); //Simular que se guardó con un ID
            return c;
        });

        personService.updatePerson(personId, requestDTO);

        verify(contactRepository, times(1)).save(argThat(contactTest ->
                contactTest.getValue().equals("nuevo@email.com") &&
                        contactTest.getLabel().equals("Email") &&
                        contactTest.getContactType().getName().equals("EMAIL") &&
                        contactTest.getPerson().equals(existingPerson)
        ));
    }

    @Test
    void testUpdatePerson_DeletesRemovedContact() {
        Contact contactToDelete = Contact.builder()
                .id(99L)
                .value("eliminar@test.com")
                .contactType(contactType)
                .build();

        existingPerson.getContacts().add(contact);
        existingPerson.getContacts().add(contactToDelete);

        requestDTO.setContactDTOS(List.of(
                ReqResContactDTO.builder()
                        .id(contactId)
                        .value("web.com")
                        .contactType("INSTAGRAM")
                        .build()
        ));

        personService.updatePerson(personId, requestDTO);

        verify(contactRepository, times(1)).delete(contactToDelete);
        verify(contactRepository, never()).delete(contact);
    }

    @Test
    void testUpdatePerson_CreatesNewContactType() {
        ReqResContactDTO contactDTO = ReqResContactDTO.builder()
                .id(null)
                .value("nuevo_valor")
                .contactType("NUEVO_TIPO")
                .build();

        requestDTO.setContactDTOS(List.of(contactDTO));

        ContactType newType = ContactType.builder().id(88L).name("NUEVO_TIPO").build();

        when(contactTypeService.getContactTypeByName("NUEVO_TIPO"))
                .thenThrow(new EntityNotFoundException());
        when(contactTypeRepository.save(any(ContactType.class))).thenReturn(newType);

        personService.updatePerson(personId, requestDTO);

        verify(contactTypeRepository, times(1)).save(argThat(ct ->
                ct.getName().equals("NUEVO_TIPO")
        ));
    }

    @Test
    void testUpdatePerson_CreatesNewIdentification() {
        IdentificationDTO newIdDTO = IdentificationDTO.builder()
                .id(null) //ID null para simular nueva identificación
                .description("87654321")
                .identificationTypeDTO(IdentificationTypeDTO.builder()
                        .id(1L)
                        .description("DNI")
                        .build())
                .build();

        requestDTO.setIdentificationDTO(List.of(newIdDTO));

        when(identificationRepository.save(any(Identification.class))).thenAnswer(invocation -> {
            Identification id = invocation.getArgument(0);
            id.setId(77L);
            return id;
        });

        personService.updatePerson(personId, requestDTO);

        verify(identificationRepository, times(1)).save(argThat(identificationTest ->
                identificationTest.getDescription().equals("87654321") &&
                        identificationTest.getDocumentType().equals(idType) &&
                        identificationTest.getPerson().equals(existingPerson)
        ));
    }

    @Test
    void testUpdatePerson_DeletesRemovedIdentification() {
        Identification idToDelete = Identification.builder()
                .id(66L)
                .description("99999999")
                .documentType(idType)
                .build();

        existingPerson.getIdentifications().add(identification);
        existingPerson.getIdentifications().add(idToDelete);

        requestDTO.setIdentificationDTO(List.of(
                IdentificationDTO.builder()
                        .id(identificationId)
                        .description("12345678")
                        .identificationTypeDTO(IdentificationTypeDTO.builder()
                                .id(1L)
                                .description("DNI")
                                .build())
                        .build()
        ));

        personService.updatePerson(personId, requestDTO);

        verify(identificationRepository, times(1)).delete(idToDelete);
        verify(identificationRepository, never()).delete(identification);
    }

    @Test
    void testUpdatePerson_CreatesNewIdentificationType() {
        IdentificationDTO idDTO = IdentificationDTO.builder()
                .id(null)
                .description("12345678")
                .identificationTypeDTO(IdentificationTypeDTO.builder()
                        .id(999L) //ID que no existe
                        .description("PASAPORTE")
                        .build())
                .build();

        requestDTO.setIdentificationDTO(List.of(idDTO));

        IdentificationType newType = IdentificationType.builder()
                .id(999L)
                .description("PASAPORTE")
                .build();

        when(identificationTypeRepository.findById(999L)).thenReturn(Optional.empty());
        when(identificationTypeRepository.save(any(IdentificationType.class))).thenReturn(newType);

        personService.updatePerson(personId, requestDTO);

        verify(identificationTypeRepository, times(1)).save(argThat(it ->
                it.getDescription().equals("PASAPORTE")
        ));
    }



}
