package com.techforb.apiportalrecruiting.modules.portal.applications.services;

import com.techforb.apiportalrecruiting.core.entities.Person;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.person.PersonResponseDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.person.PersonUpdateDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.person.PersonProfileResponseDTO;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;

public interface PersonService {

	Person getPersonById(Long id);
	PersonResponseDTO updatePersonProfile(PersonUpdateDTO updateDTO);
	Person createPerson(UserEntity user);
	PersonProfileResponseDTO getPersonProfile();

}
