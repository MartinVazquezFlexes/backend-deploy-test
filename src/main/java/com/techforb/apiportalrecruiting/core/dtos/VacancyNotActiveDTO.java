package com.techforb.apiportalrecruiting.core.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Data Transfer Object (DTO) for representing vacancy desactivated.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VacancyNotActiveDTO {
    private Long id;
    private Boolean active;
}
