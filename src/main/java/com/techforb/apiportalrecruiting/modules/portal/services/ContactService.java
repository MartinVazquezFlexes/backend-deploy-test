package com.techforb.apiportalrecruiting.modules.portal.services;

import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.contacts.RequestContactDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.contacts.ResponseContactDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.person.ContactUpdateDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ContactService {

	List<ResponseContactDTO> getContactsByPersonId();

	ResponseContactDTO getContactById(Long id);

	ResponseContactDTO createContact(RequestContactDTO contact);

	ResponseContactDTO updateContact(Long id, RequestContactDTO contact);

	void deleteContactById(Long id);
	
	void updatePersonContacts(List<ContactUpdateDTO> contacts);
	
	void deleteAllPersonContacts();
}
