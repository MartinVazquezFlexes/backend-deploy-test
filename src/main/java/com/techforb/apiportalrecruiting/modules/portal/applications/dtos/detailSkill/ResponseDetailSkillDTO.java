package com.techforb.apiportalrecruiting.modules.portal.applications.dtos.detailskill;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDetailSkillDTO {
    private Long id;

    private String descriptionSkill;

    private com.techforb.apiportalrecruiting.modules.portal.applications.dtos.detailSkill.LanguageDetailDTO language;

    private Boolean isObligatory;

    private Integer priority;

    private Integer yearsExperience;


}
