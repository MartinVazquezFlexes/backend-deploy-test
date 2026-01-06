package com.techforb.apiportalrecruiting.modules.portal.applications.dtos.vacancies;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.detailskill.ResponseDetailSkillDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VacancyDetailsDTO {

    private Long id;

    private String role;

    private String description;

    private Boolean active;

    private Integer yearsExperienceRequired;

    private String nameCompany;

    private List<ResponseDetailSkillDTO> skills;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expirationDate;
}
