package com.techforb.apiportalrecruiting.modules.portal.applications.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SkillItemDTO {
    private Long id;
    private String name;
}
