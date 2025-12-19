package com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestChangeCvApplicationDTO {
	Long applicationId;
	Long cvId;
}
