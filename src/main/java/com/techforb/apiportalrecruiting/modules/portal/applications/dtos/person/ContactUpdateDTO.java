package com.techforb.apiportalrecruiting.modules.portal.applications.dtos.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactUpdateDTO {
    private Long id; // null si es nuevo
    private String value;
    private String label;
    private Long contactTypeId;
}
