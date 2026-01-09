package com.techforb.apiportalrecruiting.modules.portal.applications.services;

import com.techforb.apiportalrecruiting.core.entities.Cv;
import com.techforb.apiportalrecruiting.core.entities.Person;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.cv.CvWithCreationDateDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.cv.ResponsePagCvDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface CvService {

	Cv uploadCv(MultipartFile cvFile, Person person, String paramFolder, boolean fromProfile);

	Cv findCvById(Long id);

	Page<ResponsePagCvDTO> getFilteredCvs(String country, String skill, Pageable pageable);

  Page<CvWithCreationDateDTO> getCvsById(Long id, Boolean isLast, Pageable pageable);

	boolean deleteCvByIdAndPersonId(Long cvId,Long personId) throws IOException;

}
