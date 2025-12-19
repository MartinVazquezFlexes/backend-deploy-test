package com.techforb.apiportalrecruiting.core.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for representing vacancy details.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VacancyDTO {

    /**
     * Unique identifier of the vacancy.
     */
    private Long id;

    /**
     * Role or job title of the vacancy.
     */
    private String role;

    /**
     * Description of the vacancy.
     */
    private String description;

    /**
     * Indicates whether the vacancy is active.
     */
    private Boolean active;

    /**
     * Required years of experience for the vacancy.
     */
    @JsonProperty("years_experience_required")
    private Integer yearsExperienceRequired;

    /**
     * Date and time when the vacancy was created.
     * Formatted as "yyyy-MM-dd HH:mm:ss".
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("creation_date")
    private LocalDateTime creationDate;

    /**
     * Language details associated with the vacancy.
     */
    @Nullable
    private LanguageDetailDTO language;
}
