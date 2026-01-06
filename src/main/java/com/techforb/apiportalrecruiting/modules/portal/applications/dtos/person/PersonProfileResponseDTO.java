package com.techforb.apiportalrecruiting.modules.portal.applications.dtos.person;

import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.SkillDTO;
import com.techforb.apiportalrecruiting.modules.portal.direction.dto.CountryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonProfileResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private CountryDTO country;
    private List<FunctionalRoleDTO> functionalRoles;
    private List<LanguageDTO> languages;
    private List<SkillDTO> skills;
    private List<ContactResponseDTO> contacts;
}
