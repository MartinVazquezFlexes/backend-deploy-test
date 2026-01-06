package com.techforb.apiportalrecruiting.modules.portal.services;


import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.contactTypes.RequestContactTypeDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.contactTypes.ResponseContactTypeDTO;
import com.techforb.apiportalrecruiting.core.entities.ContactType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ContactTypeService {

	ContactType getContactTypeByName(String name);

	List<ResponseContactTypeDTO> getAllContactTypes();

	ResponseContactTypeDTO getContactTypeById(Long id);

	ContactType getContactTypeEntityById(Long id);

	ResponseContactTypeDTO createContactType(RequestContactTypeDTO requestContactTypeDTO);

	ResponseContactTypeDTO updateContactType(Long id, RequestContactTypeDTO requestContactTypeDTO);

	void deleteContactType(Long id);
}
