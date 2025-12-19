package com.techforb.apiportalrecruiting.core.dtos;

import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.detailSkill.RequestDetailSkillDTO;
import jakarta.validation.constraints.NotNull;
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
public class RequestFullVacancyDTO {
    @NotNull
    private Long idCompany;
    @NotNull
    private Long idRecruiter;
    private String role;
    private String description;
    private Boolean active;
    private Integer yearsExperienceRequired;
    private LocalDateTime expirationDate;
    private List<RequestDetailSkillDTO> detailsSkills;
}
