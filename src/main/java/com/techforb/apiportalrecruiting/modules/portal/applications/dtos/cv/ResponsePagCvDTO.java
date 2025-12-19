package com.techforb.apiportalrecruiting.modules.portal.applications.dtos.cv;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponsePagCvDTO {

	private String cvUrl;
	private String cvName;
	private String personEmail;
	private String personCountry;
	private List<String> personSkills;
}
