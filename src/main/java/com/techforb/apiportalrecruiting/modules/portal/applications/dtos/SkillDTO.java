package com.techforb.apiportalrecruiting.modules.portal.applications.dtos;

import com.techforb.apiportalrecruiting.core.entities.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SkillDTO {
    private Long id;
    private String description;
    private CategoryDTO categoryDTO;
}
