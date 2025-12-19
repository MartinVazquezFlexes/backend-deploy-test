package com.techforb.apiportalrecruiting.modules.portal.person.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.techforb.apiportalrecruiting.core.dtos.contacts.ReqResContactDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.SkillDTO;
import com.techforb.apiportalrecruiting.modules.portal.identification.dto.IdentificationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private Long directionId;
    private String directionDescription;
    private String city;
    private String zipCode;
    private String province;
    private String country;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate dateBirth;
    private List<SkillDTO> skillDTO;
    private List<ReqResContactDTO> contactDTOS;
    private List<IdentificationDTO>identificationDTO;
}
