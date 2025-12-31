package com.techforb.apiportalrecruiting.modules.portal.direction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DirectionDTO {
    private String description;
    private CityDTO cityDTO;
    private ZipCodeDTO zipCodeDTO;
}
