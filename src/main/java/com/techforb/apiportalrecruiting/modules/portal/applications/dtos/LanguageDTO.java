package com.techforb.apiportalrecruiting.modules.portal.applications.dtos;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class LanguageDTO {
    private Long id;
    private String languageLevel;
    private String name;
}
