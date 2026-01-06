package com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.detailskill.ResponseDetailSkillDTO;
import com.techforb.apiportalrecruiting.core.entities.ApplicationState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseApplicationDTO {
	private Long id;
	private String applicantEmail;
	private String comments;
	private String cvUrl;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime applicationDate;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateDate;
	private ApplicationState applicationState;
	private List<ResponseDetailSkillDTO> responseDetailSkillDTOS;
}
