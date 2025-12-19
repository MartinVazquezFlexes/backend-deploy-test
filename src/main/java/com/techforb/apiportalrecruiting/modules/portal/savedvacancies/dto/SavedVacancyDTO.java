package com.techforb.apiportalrecruiting.modules.portal.savedvacancies.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SavedVacancyDTO {

    @JsonProperty("saved_vacancy_id")
    private Long id;

    @JsonProperty("vacancy")
    private SavedVacancyDetailsDTO vacancy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("saved_date")
    private LocalDateTime savedDate;
} 