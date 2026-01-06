package com.techforb.apiportalrecruiting.modules.portal.applications.dtos.person;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.contacts.ReqResContactDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.SkillDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.cv.CvDTO;
import com.techforb.apiportalrecruiting.modules.portal.identification.dto.IdentificationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonResponseDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String country;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDateTime dateBirth;
    private List<SkillDTO> skillDTO;
    private List<CvDTO> cvDTO;
    private List<ReqResContactDTO> contactDTOS;
    private List<IdentificationDTO> identificationDTO;
}
