package com.techforb.apiportalrecruiting.modules.portal.applications.dtos.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LanguageDTO {
    private Long id;
    private String name;
    private String level;
}
