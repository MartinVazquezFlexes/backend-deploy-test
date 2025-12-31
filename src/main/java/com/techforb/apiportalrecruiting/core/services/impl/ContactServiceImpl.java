package com.techforb.apiportalrecruiting.core.services.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.dtos.contacts.RequestContactDTO;
import com.techforb.apiportalrecruiting.core.dtos.contacts.ResponseContactDTO;
import com.techforb.apiportalrecruiting.core.entities.Contact;
import com.techforb.apiportalrecruiting.core.entities.ContactType;
import com.techforb.apiportalrecruiting.core.exceptions.UnauthorizedActionException;
import com.techforb.apiportalrecruiting.core.repositories.ContactRepository;
import com.techforb.apiportalrecruiting.core.services.ContactService;
import com.techforb.apiportalrecruiting.core.services.ContactTypeService;
import com.techforb.apiportalrecruiting.modules.backoffice.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

	private final ContactRepository contactRepository;
	private final ModelMapper modelMapper;
	private final LocalizedMessageService localizedMessageService;
	private final UserService userService;
	private final ContactTypeService contactTypeService;

	@Override
    public List<ResponseContactDTO> getContactsByPersonId() {
        return contactRepository.findByPerson_Id(getPersonId())
                .stream()
                .map(contact -> {
                    ResponseContactDTO dto = modelMapper.map(contact, ResponseContactDTO.class);
                    dto.setContactType(contact.getContactType().getName());
                    dto.setFullName(contact.getPerson().getFirstName() + " " + contact.getPerson().getLastName());
                    return dto;
                })
                .toList();
    }

	@Override
	public ResponseContactDTO getContactById(Long id) {

		Contact contact = getContactEntity(id);
		ResponseContactDTO dto = modelMapper.map(contact, ResponseContactDTO.class);
		dto.setContactType(contact.getContactType().getName());
		dto.setFullName(contact.getPerson().getFirstName() + " " + contact.getPerson().getLastName());
		return dto;
	}

	@Override
	public ResponseContactDTO createContact(RequestContactDTO requestContactDTO) {
		Contact contact = new Contact();
		contact.setLabel(requestContactDTO.getLabel());
		contact.setValue(requestContactDTO.getValue());
		contact.setPerson(userService.getUserFromContext().getPerson());

		ContactType type = contactTypeService.getContactTypeEntityById(requestContactDTO.getContactTypeId());
		contact.setContactType(type);

		contactRepository.save(contact);
		ResponseContactDTO responseContactDTO = modelMapper.map(contact, ResponseContactDTO.class);
		responseContactDTO.setContactType(contact.getContactType().getName());
		responseContactDTO.setFullName(contact.getPerson().getFirstName() + " " + contact.getPerson().getLastName());
		return responseContactDTO;
	}

	@Override
	public ResponseContactDTO updateContact(Long id, RequestContactDTO requestContactDTO) {
		Contact contact = getContactEntity(id);

		contact.setValue(requestContactDTO.getValue());
		contact.setLabel(requestContactDTO.getLabel());

		ContactType type = contactTypeService.getContactTypeEntityById(requestContactDTO.getContactTypeId());
		contact.setContactType(type);

		contactRepository.save(contact);

		ResponseContactDTO responseContactDTO = modelMapper.map(contact, ResponseContactDTO.class);
		responseContactDTO.setContactType(type.getName());
		responseContactDTO.setFullName(contact.getPerson().getFirstName() + " " + contact.getPerson().getLastName());
		return responseContactDTO;
	}

	@Override
	public void deleteContactById(Long id) {
		getContactEntity(id);
		contactRepository.deleteById(id);
	}

	private Long getPersonId() {
		return userService.getUserFromContext().getPerson().getId();
	}

	private Contact getContactEntity(Long id) {
		Contact contact = contactRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(localizedMessageService.getMessage("contact.not_found_by_id", id)));

		if (!Objects.equals(contact.getPerson().getId(), getPersonId())) {
			throw new UnauthorizedActionException(localizedMessageService.getMessage("user.without_permissions"));
		}
		return contact;
	}
}
