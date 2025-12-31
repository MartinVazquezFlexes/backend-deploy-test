package com.techforb.apiportalrecruiting.modules.portal.direction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CityDTO {
    private String name;
    private ProvinceDTO provinceDTO;
}
