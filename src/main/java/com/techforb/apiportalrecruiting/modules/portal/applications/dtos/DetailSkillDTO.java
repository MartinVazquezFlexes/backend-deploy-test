package com.techforb.apiportalrecruiting.modules.portal.applications.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailSkillDTO {
    private Long id;
    private String skill;//descriptionskill
    private Boolean isObligatory;
    private Integer priority;
    private Integer yearsExperience;
}