package com.techforb.apiportalrecruiting.modules.portal.applications.dtos.contacts;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseContactDTO {
	private Long id;

	private String contactType;

	private String value;

	private String label;

	private String fullName;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Argentina/Buenos_Aires")
	private LocalDateTime createdAt;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Argentina/Buenos_Aires")
	private LocalDateTime updatedAt;
}
