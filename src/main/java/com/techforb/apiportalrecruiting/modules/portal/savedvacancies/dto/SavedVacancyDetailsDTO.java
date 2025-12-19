package com.techforb.apiportalrecruiting.modules.portal.savedvacancies.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SavedVacancyDetailsDTO {

    private Long id;

    private String role;

    private String description;

    private Boolean active;

    private Integer yearsExperienceRequired;

    private String nameCompany;

    private String direction;

    private String workModality;

    private List<ResponseDetailSkillWithoutPriorityDTO> skills;
} 