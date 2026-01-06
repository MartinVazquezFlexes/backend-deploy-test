package com.techforb.apiportalrecruiting.modules.portal.applications.dtos.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactResponseDTO {
    private Long id;
    private String value;
    private String label;
    private String contactType;
}
