package com.techforb.apiportalrecruiting.modules.portal.applications.services.impl;

import com.techforb.apiportalrecruiting.core.dtos.contacts.ReqResContactDTO;
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
import com.techforb.apiportalrecruiting.core.services.ContactTypeService;
import com.techforb.apiportalrecruiting.modules.backoffice.user.UserRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.SkillDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.PersonRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.SkillRepository;
import com.techforb.apiportalrecruiting.modules.portal.direction.repository.CityRepository;
import com.techforb.apiportalrecruiting.modules.portal.direction.repository.CountryRepository;
import com.techforb.apiportalrecruiting.modules.portal.direction.repository.DirectionRepository;
import com.techforb.apiportalrecruiting.modules.portal.direction.repository.ProvinceRepository;
import com.techforb.apiportalrecruiting.modules.portal.direction.repository.ZipCodeRepository;
import com.techforb.apiportalrecruiting.modules.portal.identification.dto.IdentificationDTO;
import com.techforb.apiportalrecruiting.modules.portal.identification.dto.IdentificationTypeDTO;
import com.techforb.apiportalrecruiting.core.entities.Identification;
import com.techforb.apiportalrecruiting.core.entities.IdentificationType;
import com.techforb.apiportalrecruiting.modules.portal.identification.repository.IdentificationRepository;
import com.techforb.apiportalrecruiting.modules.portal.identification.repository.IdentificationTypeRepository;
import com.techforb.apiportalrecruiting.modules.portal.person.dto.PersonRequestDTO;
import com.techforb.apiportalrecruiting.modules.portal.person.dto.PersonResponseDTO;
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
import static org.mockito.Mockito.when;
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
    private Long directionId = 1L;
    private Long skillId = 1L;
    private Long contactTypeId = 1L;
    private Long contactId = 1L;
    private Long identificationId = 1L;
    private PersonRequestDTO requestDTO;
    private Person existingPerson;
    private Direction direction;
    private Skill skill;
    private ContactType contactType;
    private Contact contact;
    private IdentificationType idType;
    private Identification identification;
    private UserEntity user;
    private Country country;
    private Province province;
    private City city;
    private ZipCode zipCode;

    @BeforeEach
    void setUp() {
        country = Country.builder().id(1L).name("Argentina").build();
        province = Province.builder().id(1L).name("Buenos Aires").country(country).build();
        city = City.builder().id(1L).name("CABA").province(province).build();
        zipCode = ZipCode.builder().id(1L).name("1000").build();

        direction = Direction.builder()
                .id(directionId)
                .description("Calle falsa 123")
                .city(city)
                .zipCode(zipCode)
                .build();

        user = UserEntity.builder().email("juan@example.com").build();

        skill = Skill.builder().id(skillId).description("Java").category(Category.builder().name("Backend").build()).build();

        contactType = ContactType.builder().id(contactTypeId).name("INSTAGRAM").build();

        contact = Contact.builder().id(contactId).value("web.com").contactType(contactType).build();

        idType = IdentificationType.builder().id(1L).description("DNI").build();

        identification = Identification.builder()
                .id(identificationId)
                .description("12345678")
                .documentType(idType)
                .build();

        existingPerson = Person.builder()
                .id(personId)
                .firstName("Juan")
                .lastName("Pérez")
                .dateBirth(LocalDateTime.now())
                .user(user)
                .direction(direction)
                .skills(new ArrayList<>())
                .contacts(new ArrayList<>())
                .identifications(new ArrayList<>())
                .cvs(new ArrayList<>())
                .build();

        requestDTO = PersonRequestDTO.builder()
                .firstName("Daniel")
                .lastName("Espinoza")
                .email("juan@example.com")
                .directionId(directionId)
                .directionDescription(direction.getDescription())
                .city(city.getName())
                .zipCode(zipCode.getName())
                .province(province.getName())
                .country(country.getName())
                .dateBirth(LocalDate.of(1993, 3, 22))
                .skillDTO(List.of(SkillDTO.builder().id(skillId).build()))
                .contactDTOS(List.of(ReqResContactDTO.builder().id(contactId).value("web.com").contactType("INSTAGRAM").build()))
                .identificationDTO(List.of(IdentificationDTO.builder()
                        .id(identificationId)
                        .description("12345678")
                        .identificationTypeDTO(IdentificationTypeDTO.builder().id(1L).description("DNI").build())
                        .build()))
                .build();

        // Mocks de repositorios
        when(personRepository.findById(personId)).thenReturn(Optional.of(existingPerson));
        when(directionRepository.findById(directionId)).thenReturn(Optional.of(direction));
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(contactTypeRepository.findByName("INSTAGRAM")).thenReturn(contactType);
        when(contactTypeRepository.findById(contactTypeId)).thenReturn(Optional.of(contactType));
        when(contactTypeService.getContactTypeByName("INSTAGRAM")).thenReturn(contactType);
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));
        when(identificationTypeRepository.findById(1L)).thenReturn(Optional.of(idType));
        when(identificationRepository.findById(identificationId)).thenReturn(Optional.of(identification));
        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(user));
        when(personRepository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(countryRepository.findByNameIgnoreCase("Argentina")).thenReturn(Optional.of(country));
        when(provinceRepository.findByNameIgnoreCase("Buenos Aires")).thenReturn(Optional.of(province));
        when(cityRepository.findByNameIgnoreCase("CABA")).thenReturn(Optional.of(city));
        when(zipCodeRepository.findByName("1000")).thenReturn(Optional.of(zipCode));
    }

    @Test
    void updatePerson_successfulUpdate_returnsUpdatedDTO() {

        PersonResponseDTO response = personService.updatePerson(personId, requestDTO);

        assertNotNull(response);
        assertEquals("Daniel", response.getFirstName());
        assertEquals("Espinoza", response.getLastName());
        assertEquals("juan@example.com", response.getEmail());
        assertEquals(1, response.getSkillDTO().size());
        assertEquals("Java", response.getSkillDTO().get(0).getDescription());
        assertEquals(1, response.getContactDTOS().size());
        assertEquals("INSTAGRAM", response.getContactDTOS().get(0).getContactType());
        assertEquals("web.com", response.getContactDTOS().get(0).getValue());
        assertEquals(1, response.getIdentificationDTO().size());
        assertEquals("12345678", response.getIdentificationDTO().get(0).getDescription());
    }

    @Test
    void updatePerson_personNotFound_throwsException() {
        when(personRepository.findById(personId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> personService.updatePerson(personId, requestDTO));
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
}
