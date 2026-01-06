package com.techforb.apiportalrecruiting.modules.portal.savedvacancies.dto;

import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.detailSkill.LanguageDetailDTO;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDetailSkillWithoutPriorityDTO {
    private Long id;

    private String descriptionSkill;

    private LanguageDetailDTO language;

    private Boolean isObligatory;

    private Integer yearsExperience;
} 