package com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.detailskill.ResponseDetailSkillDTO;
import com.techforb.apiportalrecruiting.core.entities.ApplicationState;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.cv.CvDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.LanguageDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.VacancyDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ApplicationDTO {
    private Long id;
    private String comments;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime applicationDate;
    private ApplicationState applicationState;
    private VacancyDTO vacancy;
    private CvDTO cv;
    private List<ResponseDetailSkillDTO> detailSkill;
    private List<LanguageDTO> language;
}
