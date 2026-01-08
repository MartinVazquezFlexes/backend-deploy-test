package com.techforb.apiportalrecruiting.modules.portal.services.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.contacts.RequestContactDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.contacts.ResponseContactDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.person.ContactUpdateDTO;
import com.techforb.apiportalrecruiting.core.entities.Contact;
import com.techforb.apiportalrecruiting.core.entities.ContactType;
import com.techforb.apiportalrecruiting.core.exceptions.UnauthorizedActionException;
import com.techforb.apiportalrecruiting.core.repositories.ContactRepository;
import com.techforb.apiportalrecruiting.modules.portal.services.ContactService;
import com.techforb.apiportalrecruiting.modules.portal.services.ContactTypeService;
import com.techforb.apiportalrecruiting.core.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
	@Transactional
	public void deleteContactById(Long id) {
		Long personId = getPersonId();

		long deleted = contactRepository.deleteByIdAndPerson_Id(id, personId);

		if (deleted == 0) {
			throw new EntityNotFoundException(
					localizedMessageService.getMessage("contact.not_found_by_id", id)
			);
		}
	}


	@Override
	@Transactional
	public void updatePersonContacts(List<ContactUpdateDTO> contacts) {
		Long personId = getPersonId();
		
		// Si la lista es null o vacía, eliminar todos los contactos
		if (contacts == null || contacts.isEmpty()) {
			deleteAllPersonContacts();
			return;
		}
		
		// Obtener contactos existentes de la persona
		List<Contact> existingContacts = contactRepository.findByPerson_Id(personId);
		
		// Procesar cada contacto del DTO
		for (ContactUpdateDTO contactDTO : contacts) {
			if (contactDTO.getId() != null && existingContacts.stream()
					.anyMatch(c -> c.getId().equals(contactDTO.getId()))) {
				// Actualizar contacto existente
				updateExistingContact(contactDTO);
			} else {
				// Crear nuevo contacto
				createNewContact(contactDTO);
			}
		}
		
		// Eliminar contactos que ya no están en la lista
		removeDeletedContacts(existingContacts, contacts);
	}
	
	@Override
	@Transactional
	public void deleteAllPersonContacts() {
		Long personId = getPersonId();
		List<Contact> personContacts = contactRepository.findByPerson_Id(personId);
		contactRepository.deleteAll(personContacts);
	}
	
	private void updateExistingContact(ContactUpdateDTO contactDTO) {
		RequestContactDTO requestDTO = RequestContactDTO.builder()
			.value(contactDTO.getValue())
			.label(contactDTO.getLabel())
			.contactTypeId(contactDTO.getContactTypeId())
			.build();
		
		updateContact(contactDTO.getId(), requestDTO);
	}
	
	private void createNewContact(ContactUpdateDTO contactDTO) {
		RequestContactDTO requestDTO = RequestContactDTO.builder()
			.value(contactDTO.getValue())
			.label(contactDTO.getLabel())
			.contactTypeId(contactDTO.getContactTypeId())
			.build();
		
		createContact(requestDTO);
	}
	
	private void removeDeletedContacts(List<Contact> existingContacts, List<ContactUpdateDTO> contacts) {
		List<Long> contactIdsToKeep = contacts.stream()
			.map(ContactUpdateDTO::getId)
			.filter(id -> id != null)
			.collect(Collectors.toList());
		
		// Obtener contactos que deben ser eliminados
		List<Contact> contactsToDelete = existingContacts.stream()
			.filter(contact -> !contactIdsToKeep.contains(contact.getId()))
			.collect(Collectors.toList());
		
		// Eliminar contactos de la base de datos
		contactRepository.deleteAll(contactsToDelete);
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
