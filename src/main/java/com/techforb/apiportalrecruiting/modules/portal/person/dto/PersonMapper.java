package com.techforb.apiportalrecruiting.modules.portal.person.dto;

import com.techforb.apiportalrecruiting.core.dtos.contacts.ReqResContactDTO;
import com.techforb.apiportalrecruiting.core.entities.*;
import com.techforb.apiportalrecruiting.core.security.cloudinary.CloudinaryService;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.CategoryDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.CvDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.SkillDTO;
import com.techforb.apiportalrecruiting.modules.portal.identification.dto.IdentificationDTO;
import com.techforb.apiportalrecruiting.modules.portal.identification.dto.IdentificationTypeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PersonMapper {

    private final CloudinaryService cloudinaryService;

    public PersonResponseDTO mapToPersonDTO(Person person) {
        Direction direction = person.getDirection();
        City city = direction != null ? direction.getCity() : null;
        Province province = city != null ? city.getProvince() : null;
        Country country = province != null ? province.getCountry() : null;

        if (person == null) return null;
        return PersonResponseDTO.builder()
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .email(person.getUser().getEmail())
                .directionId(direction != null ? direction.getId() : null)
                .directionDescription(direction != null ? direction.getDescription() : null)
                .city(city != null ? city.getName() : null)
                .zipCode(direction != null && direction.getZipCode() != null ? direction.getZipCode().getName() : null)
                .province(province != null ? province.getName() : null)
                .country(country != null ? country.getName() : null)
                .dateBirth(person.getDateBirth())
                .skillDTO(mapToSkillListDTO(person.getSkills()))
                .cvDTO(mapToListCvDTO(person.getCvs()))
                .contactDTOS(mapToContactListDTO(person.getContacts()))
                .identificationDTO(mapToIdentificationListDTO(person.getIdentifications()))
                .build();
    }

    public List<CvDTO> mapToListCvDTO(List<Cv> cvList) {
        if (cvList == null) return null;
        return cvList.stream()
                .map(cv -> CvDTO.builder()
                        .id(cv.getId())
                        .name(cv.getName())
                        .cvUrl(cloudinaryService.generateSignedUrl(cv.getPublicId(), cv.getVersion()))
                        .build())
                .collect(Collectors.toList());
    }

    public List<ReqResContactDTO> mapToContactListDTO(List<Contact> contactDTOList) {
        if (contactDTOList == null) return null;
        return contactDTOList.stream()
                .map(contact -> ReqResContactDTO.builder()
                        .id(contact.getId())
                        .contactType(contact.getContactType().getName())
                        .value(contact.getValue())
                        .label(contact.getLabel())
                        .build())
                .collect(Collectors.toList());
    }

    public List<IdentificationDTO> mapToIdentificationListDTO(List<Identification> identificationList) {
        if (identificationList == null) return null;
        return identificationList.stream()
                .map(identification -> IdentificationDTO.builder()
                        .id(identification.getId())
                        .description(identification.getDescription())
                        .identificationTypeDTO(mapToIdentificationTypeDTO(identification.getDocumentType()))
                        .build())
                .collect(Collectors.toList());
    }

    public IdentificationTypeDTO mapToIdentificationTypeDTO(IdentificationType identificationType) {
        if (identificationType == null) return null;
        return IdentificationTypeDTO.builder()
                .id(identificationType.getId())
                .description(identificationType.getDescription())
                .build();
    }

    public List<SkillDTO> mapToSkillListDTO(List<Skill> skillList) {
        if (skillList == null) return null;
        return skillList.stream()
                .map(skill -> SkillDTO.builder()
                        .id(skill.getId())
                        .description(skill.getDescription())
                        .categoryDTO(mapToCategoryDTO(skill.getCategory()))
                        .build())
                .collect(Collectors.toList());
    }

    public CategoryDTO mapToCategoryDTO(Category category) {
        if (category == null) return null;
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
