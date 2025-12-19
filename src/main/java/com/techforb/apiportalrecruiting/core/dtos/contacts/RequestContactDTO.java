package com.techforb.apiportalrecruiting.core.dtos.contacts;

import com.google.firebase.database.annotations.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestContactDTO {

	@NotNull
	private Long contactTypeId;

	@NotBlank
	private String value;

	private String label;
}
