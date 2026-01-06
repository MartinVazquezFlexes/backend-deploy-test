package com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application;

import com.techforb.apiportalrecruiting.core.entities.ApplicationState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationStateUpdateDTO {
    private ApplicationState applicationState;
}
