package com.techforb.apiportalrecruiting.modules.portal.applications.dtos.cv;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CvDTO {
    private Long id;
    private String cvUrl;
    private String name;
}