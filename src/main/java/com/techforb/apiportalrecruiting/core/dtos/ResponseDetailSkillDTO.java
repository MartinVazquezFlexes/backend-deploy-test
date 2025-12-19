package com.techforb.apiportalrecruiting.core.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDetailSkillDTO {
    private Long id;

    private String descriptionSkill;

    private LanguageDetailDTO language;

    private Boolean isObligatory;

    private Integer priority;

    private Integer yearsExperience;


}
