package com.techforb.apiportalrecruiting.modules.portal.applications.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VacancyDTO {
    private Long id;
    private String companyName;
    private String role;
    private String description;
    private Boolean active;
    private Integer yearsExperienceRequired;
}
