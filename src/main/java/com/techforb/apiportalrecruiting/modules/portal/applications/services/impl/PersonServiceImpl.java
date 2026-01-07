package com.techforb.apiportalrecruiting.modules.portal.applications.services.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.entities.*;
import com.techforb.apiportalrecruiting.core.services.UserService;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.person.*;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.PersonRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.PersonService;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.SkillService;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.LanguageService;
import com.techforb.apiportalrecruiting.modules.portal.services.ContactService;
import com.techforb.apiportalrecruiting.modules.portal.services.RoleFunctionalService;
import com.techforb.apiportalrecruiting.modules.portal.direction.service.CountryService;
import com.techforb.apiportalrecruiting.modules.portal.phone.service.PhoneService;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.SkillDTO;
import com.techforb.apiportalrecruiting.modules.portal.direction.dto.CountryDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final UserService userService;
    private final SkillService skillService;
    private final LanguageService languageService;
    private final ContactService contactService;
    private final RoleFunctionalService roleFunctionalService;
    private final CountryService countryService;
    private final PhoneService phoneService;
    private final PersonMapper personMapper;
    private final LocalizedMessageService localizedMessageService;

    @Override
    public Person getPersonById(Long id) {
        return personRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(localizedMessageService.getMessage("person.not_found")));
    }

    @Override
    @Transactional
    public PersonResponseDTO updatePersonProfile(PersonUpdateDTO updateDTO) {
        // Obtener solo el ID del usuario autenticado
        UserEntity currentUser = userService.getUserFromContext();
        Long userId = currentUser.getId();

        Person person = personRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        localizedMessageService.getMessage("person.not_found")
                ));

        updateBasicPersonData(person, updateDTO);

        if (updateDTO.getCountryId() != null) {
            countryService.assignCountry(person, updateDTO.getCountryId());
        }

        if (updateDTO.getFunctionalRoleId() != null) {
            roleFunctionalService.assignRoleFunctional(person, updateDTO.getFunctionalRoleId());
        }

        if (updateDTO.getLanguageId() != null) {
            languageService.assignLanguageToPerson(person, updateDTO.getLanguageId());
        }

        if (updateDTO.getSkillIds() != null) {
            skillService.assignPersonSkills(person, updateDTO.getSkillIds());
        }

        if (updateDTO.getPhoneNumber() != null) {
            phoneService.updatePhoneForPerson(person.getId(), updateDTO.getPhoneNumber());
        }

        if (updateDTO.getContacts() != null) {
            contactService.updatePersonContacts(updateDTO.getContacts());
        }

        personRepository.saveAndFlush(person);

        return personMapper.mapToPersonDTO(person);
    }

    private void updateBasicPersonData(Person person, PersonUpdateDTO updateDTO) {
        if (updateDTO.getFirstName() != null) {
            person.setFirstName(updateDTO.getFirstName());
        }
        if (updateDTO.getLastName() != null) {
            person.setLastName(updateDTO.getLastName());
        }
    }

    @Override
    public Person createPerson(UserEntity user) {
        Person newPerson = new Person();
        newPerson.setUser(user);
        return personRepository.save(newPerson);
    }

    @Override
    @Transactional
    public PersonProfileResponseDTO getPersonProfile() {
        UserEntity currentUser = userService.getUserFromContext();
        Person person = currentUser.getPerson();

        if (person == null) {
            throw new EntityNotFoundException(localizedMessageService.getMessage("person.not_found"));
        }

        return PersonProfileResponseDTO.builder()
            .id(person.getId())
            .firstName(person.getFirstName())
            .lastName(person.getLastName())
            .email(currentUser.getEmail())
            .phoneNumber(phoneService.getPhoneByPersonId(person.getId()) != null ?
                phoneService.getPhoneByPersonId(person.getId()).getPhoneNumber() : null)
            .country(mapCountry(person.getCountryResidence()))
            .functionalRoles(mapFunctionalRoles(person.getRoleFunctionals()))
            .languages(mapLanguages(person.getLanguages()))
            .skills(mapSkills(person.getSkills()))
            .contacts(mapContacts(person.getContacts()))
            .build();
    }

    private CountryDTO mapCountry(Country country) {
        if (country == null) return null;
        return CountryDTO.builder()
            .id(country.getId())
            .name(country.getName())
            .build();
    }

    private List<FunctionalRoleDTO> mapFunctionalRoles(List<RoleFunctional> roleFunctionals) {
        if (roleFunctionals == null) return List.of();
        return roleFunctionals.stream()
            .map(role -> FunctionalRoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .build())
            .collect(Collectors.toList());
    }

    private List<LanguageDTO> mapLanguages(List<Language> languages) {
        if (languages == null) return List.of();
        return languages.stream()
            .map(lang -> LanguageDTO.builder()
                .id(lang.getId())
                .name(lang.getName())
                .level(lang.getLanguageLevel())
                .build())
            .collect(Collectors.toList());
    }

    private List<SkillDTO> mapSkills(List<Skill> skills) {
        if (skills == null) return List.of();
        return skills.stream()
            .map(skill -> SkillDTO.builder()
                .id(skill.getId())
                .description(skill.getDescription())
                .category(skill.getCategory() != null ? skill.getCategory().getName() : null)
                .build())
            .collect(Collectors.toList());
    }

    private List<ContactResponseDTO> mapContacts(List<Contact> contacts) {
        if (contacts == null) return List.of();
        return contacts.stream()
            .map(contact -> ContactResponseDTO.builder()
                .id(contact.getId())
                .value(contact.getValue())
                .label(contact.getLabel())
                .contactType(contact.getContactType().getName())
                .build())
            .collect(Collectors.toList());
    }
}
