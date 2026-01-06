package com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application;

import com.techforb.apiportalrecruiting.core.entities.ApplicationState;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.DetailSkillUpdateDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationModified {
    private String comments;
    private ApplicationState applicationState;
    private Long cvId;
    private List<DetailSkillUpdateDTO> detailSkills;
    private Long languageId;
}
