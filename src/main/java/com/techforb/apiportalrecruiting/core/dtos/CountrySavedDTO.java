package com.techforb.apiportalrecruiting.core.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CountrySavedDTO {
    private Long id;
    private String name;
}
