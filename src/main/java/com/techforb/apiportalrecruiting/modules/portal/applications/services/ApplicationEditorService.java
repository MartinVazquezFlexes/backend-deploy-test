package com.techforb.apiportalrecruiting.modules.portal.applications.services;

import com.techforb.apiportalrecruiting.core.entities.ApplicationState;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.ApplicationDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.ApplicationModified;
import java.util.List;

public interface ApplicationEditorService {

     List<ApplicationDTO> getApplicationByApplicantId(Long id);
     ApplicationDTO modifyApplication(Long id, ApplicationModified applicationModified);
     ApplicationDTO modifyStateApplication(Long id, ApplicationState state);
}
