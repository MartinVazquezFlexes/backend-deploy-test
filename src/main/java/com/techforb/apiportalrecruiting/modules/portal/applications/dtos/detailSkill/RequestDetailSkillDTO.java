package com.techforb.apiportalrecruiting.modules.portal.applications.dtos.detailskill;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDetailSkillDTO {
	private Long vacancyId;
	private Long skillId;
	private Long applicationId;
	private Long languageId;
	@NotNull
	private Boolean isObligatory;
	@NotNull
	private Integer priority;
	@NotNull
	private Integer yearsExperience;
}
