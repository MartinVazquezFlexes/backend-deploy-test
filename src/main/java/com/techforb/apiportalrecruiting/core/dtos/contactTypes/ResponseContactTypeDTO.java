package com.techforb.apiportalrecruiting.core.dtos.contactTypes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseContactTypeDTO {
	private Long id;

	private String name;
}
