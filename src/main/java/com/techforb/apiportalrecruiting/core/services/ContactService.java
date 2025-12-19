package com.techforb.apiportalrecruiting.core.services;

import com.techforb.apiportalrecruiting.core.dtos.contacts.RequestContactDTO;
import com.techforb.apiportalrecruiting.core.dtos.contacts.ResponseContactDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ContactService {

	List<ResponseContactDTO> getContactsByPersonId();

	ResponseContactDTO getContactById(Long id);

	ResponseContactDTO createContact(RequestContactDTO contact);

	ResponseContactDTO updateContact(Long id, RequestContactDTO contact);

	void deleteContactById(Long id);
}
