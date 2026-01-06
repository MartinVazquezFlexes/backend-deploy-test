package com.techforb.apiportalrecruiting.modules.portal.applications.dtos.contacts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqResContactDTO {

	private Long id;
	private String contactType;
	private String value;
	private String label;
}
