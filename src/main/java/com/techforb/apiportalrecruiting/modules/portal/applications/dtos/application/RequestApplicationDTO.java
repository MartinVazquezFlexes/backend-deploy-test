package com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application;

import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.detailSkill.RequestDetailSkillDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestApplicationDTO {
	private Long personId;
	private Long vacancyId;
	@NotNull
	private String comments;
	private List<RequestDetailSkillDTO> requestDetailSkillDTOS;
}
