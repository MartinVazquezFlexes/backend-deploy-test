package com.techforb.apiportalrecruiting.modules.portal.applications.dtos.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LanguageWithLevelDTO {
    private Long languageId;
    private String level; // Básico, Intermedio, Avanzado, Nativo
}
