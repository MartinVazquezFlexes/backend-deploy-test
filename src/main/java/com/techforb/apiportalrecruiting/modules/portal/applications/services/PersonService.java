package com.techforb.apiportalrecruiting.modules.portal.applications.services;

import com.techforb.apiportalrecruiting.core.entities.Person;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.modules.portal.person.dto.PersonRequestDTO;
import com.techforb.apiportalrecruiting.modules.portal.person.dto.PersonResponseDTO;


public interface PersonService {

	Person getPersonById(Long id);
	PersonResponseDTO getPersonByIdDTO (Long id);
	PersonResponseDTO updatePerson(Long id, PersonRequestDTO personRequestDTO);
	Person createPerson(UserEntity user);

}
