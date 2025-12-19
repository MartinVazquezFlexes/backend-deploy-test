package com.techforb.apiportalrecruiting.modules.portal.applications.services;

import com.techforb.apiportalrecruiting.core.entities.Application;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application.RequestApplicationDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application.RequestChangeCvApplicationDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application.ResponseApplicationDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ApplicationService {
    ResponseApplicationDTO applyVacancy(RequestApplicationDTO requestApplicationDTO, MultipartFile cvFile) throws IOException;

	Application findById(Long applicationId);

	ResponseApplicationDTO changeCvApplication(RequestChangeCvApplicationDTO requestChangeCvApplicationDTO);
}
