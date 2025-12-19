package com.techforb.apiportalrecruiting.modules.portal.applications.dtos;

import com.techforb.apiportalrecruiting.core.entities.Language;
import com.techforb.apiportalrecruiting.core.entities.Skill;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

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