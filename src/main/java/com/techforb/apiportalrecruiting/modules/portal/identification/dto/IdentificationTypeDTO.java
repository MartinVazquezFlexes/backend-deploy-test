package com.techforb.apiportalrecruiting.modules.portal.identification.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdentificationTypeDTO {
    private Long id;
    private String description;
}
