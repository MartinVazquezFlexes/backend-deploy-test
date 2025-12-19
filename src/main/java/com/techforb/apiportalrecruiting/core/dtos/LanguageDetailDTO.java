package com.techforb.apiportalrecruiting.core.dtos;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
/**
 * Data Transfer Object (DTO) for representing language details.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LanguageDetailDTO {

    /**
     * Name of the language.
     */
    @JsonProperty("language_name")
    private String name;

    /**
     * Proficiency level of the language.
     */
    @JsonProperty("language_level")
    private String languageLevel;
}