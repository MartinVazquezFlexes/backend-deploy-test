package com.techforb.apiportalrecruiting.core.services.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.dtos.contacts.RequestContactDTO;
import com.techforb.apiportalrecruiting.core.dtos.contacts.ResponseContactDTO;
import com.techforb.apiportalrecruiting.core.entities.Contact;
import com.techforb.apiportalrecruiting.core.entities.ContactType;
import com.techforb.apiportalrecruiting.core.entities.Person;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.core.exceptions.UnauthorizedActionException;
import com.techforb.apiportalrecruiting.core.repositories.ContactRepository;
import com.techforb.apiportalrecruiting.core.services.ContactService;
import com.techforb.apiportalrecruiting.core.services.ContactTypeService;
import com.techforb.apiportalrecruiting.modules.backoffice.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ActiveProfiles("test")
@SpringBootTest
class ContactServiceImplTest {

	@Autowired
	ContactService contactService;

	@MockitoBean
	ContactRepository contactRepository;

	@MockitoBean
	LocalizedMessageService localizedMessageService;

	@MockitoBean
	UserService userService;

	@MockitoBean
	ContactTypeService contactTypeService;

	@Autowired
	ModelMapper modelMapper;

	Person person;
	ResponseContactDTO responseContactDTO;
	ContactType contactType;
	Contact contact;
	List<ResponseContactDTO> responseContactDTOList;
	UserEntity user;

	@BeforeEach
	void setUp() {
		person = new Person();
		person.setId(1L);
		person.setFirstName("Tobias");
		person.setLastName("Moreno");

		user = new UserEntity();
		user.setPerson(person);

		contactType = new ContactType();
		contactType.setName("EMAIL");

		contact = new Contact();
		contact.setLabel("Trabajo");
		contact.setValue("tobias@techforb.com");
		contact.setPerson(person);
		contact.setContactType(contactType);
	}


	@Test
	void getContactsByPersonId_Success() {
		when(userService.getUserFromContext()).thenReturn(user);
		when(contactRepository.findByPerson_Id(1L)).thenReturn(List.of(contact));

		List<ResponseContactDTO> responseContactDTOS = contactService.getContactsByPersonId();

		assertEquals(1, responseContactDTOS.size());
		assertEquals("Trabajo", responseContactDTOS.get(0).getLabel());
		assertEquals("tobias@techforb.com", responseContactDTOS.get(0).getValue());
		assertEquals("EMAIL", responseContactDTOS.get(0).getContactType());
		assertEquals("Tobias Moreno", responseContactDTOS.get(0).getFullName());

	}

	@Test
	void getContactById_Success() {
		when(userService.getUserFromContext()).thenReturn(user);
		when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));

		ResponseContactDTO responseContactDTOO = contactService.getContactById(1L);

		assertEquals("Trabajo", responseContactDTOO.getLabel());
		assertEquals("tobias@techforb.com", responseContactDTOO.getValue());
		assertEquals("EMAIL", responseContactDTOO.getContactType());
		assertEquals("Tobias Moreno", responseContactDTOO.getFullName());

	}

	@Test
	void getContactById_Error_ContactNotFound() {
		when(contactRepository.findById(2L)).thenReturn(Optional.empty());
		when(localizedMessageService.getMessage("contact.not_found_by_id",2L)).thenReturn("Contact not found");

		Exception exception = assertThrows(EntityNotFoundException.class, () -> {
			contactService.getContactById(2L);
		});

		assertEquals("Contact not found", exception.getMessage());
	}

	@Test
	void getContactById_Error_UserUnauthorized() {
		Person anotherPerson = new Person();
		anotherPerson.setId(999L);
		contact.setPerson(anotherPerson);

		when(userService.getUserFromContext()).thenReturn(user);
		when(contactRepository.findById(10L)).thenReturn(Optional.of(contact));
		when(localizedMessageService.getMessage("user.without_permissions")).thenReturn("User doesn't have permissions");

		Exception exception = assertThrows(UnauthorizedActionException.class, () -> {
			contactService.getContactById(10L);
		});

		assertEquals("User doesn't have permissions", exception.getMessage());
	}

	@Test
	void createContact_Success() {
		RequestContactDTO request = new RequestContactDTO();
		request.setLabel("Trabajo");
		request.setValue("tobias@techforb.com");
		request.setContactTypeId(1L);

		when(userService.getUserFromContext()).thenReturn(user);
		when(contactTypeService.getContactTypeEntityById(1L)).thenReturn(contactType);
		when(contactRepository.save(any())).thenReturn(contact);

		ResponseContactDTO dto = contactService.createContact(request);

		verify(contactRepository).save(any(Contact.class));
		assertEquals("Trabajo", dto.getLabel());
		assertEquals("tobias@techforb.com", dto.getValue());
		assertEquals("EMAIL", dto.getContactType());
	}

	@Test
	void createContact_Error_ContactTypeNotFound() {
		RequestContactDTO request = new RequestContactDTO();
		request.setContactTypeId(999L);

		when(userService.getUserFromContext()).thenReturn(user);
		when(contactTypeService.getContactTypeEntityById(999L))
				.thenThrow(new EntityNotFoundException("Tipo no válido"));

		assertThrows(EntityNotFoundException.class, () -> {
			contactService.createContact(request);
		});
	}

	@Test
	void updateContact_Success() {
		RequestContactDTO request = new RequestContactDTO();
		request.setLabel("Personal");
		request.setValue("tobias@gmail.com");
		request.setContactTypeId(1L);

		when(userService.getUserFromContext()).thenReturn(user);
		when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));
		when(contactTypeService.getContactTypeEntityById(1L)).thenReturn(contactType);

		ResponseContactDTO dto = contactService.updateContact(1L, request);

		assertEquals("Personal", dto.getLabel());
		assertEquals("tobias@gmail.com", dto.getValue());
		assertEquals("EMAIL", dto.getContactType());
		assertEquals("Tobias Moreno", dto.getFullName());
	}

	@Test
	void updateContact_Error_ContactNotFound() {
		RequestContactDTO request = new RequestContactDTO();
		when(contactRepository.findById(99L)).thenReturn(Optional.empty());
		when(localizedMessageService.getMessage("contact.not_found_by_id", 99L)).thenReturn("No se encontró el contacto");

		assertThrows(EntityNotFoundException.class, () -> {
			contactService.updateContact(99L, request);
		});
	}

	@Test
	void updateContact_Error_Unauthorized() {
		Person anotherPerson = new Person();
		anotherPerson.setId(999L);
		contact.setPerson(anotherPerson);

		when(userService.getUserFromContext()).thenReturn(user);
		when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));
		when(localizedMessageService.getMessage("user.without_permissions")).thenReturn("Sin permisos");

		RequestContactDTO request = new RequestContactDTO();

		assertThrows(UnauthorizedActionException.class,
				() -> contactService.updateContact(1L, request));
	}

	@Test
	void deleteContactById_Success() {
		when(userService.getUserFromContext()).thenReturn(user);
		when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));

		contactService.deleteContactById(1L);

		verify(contactRepository).deleteById(1L);
	}

	@Test
	void deleteContactById_Error_Unauthorized() {
		Person anotherPerson = new Person();
		anotherPerson.setId(999L);
		contact.setPerson(anotherPerson);

		when(userService.getUserFromContext()).thenReturn(user);
		when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));
		when(localizedMessageService.getMessage("user.without_permissions")).thenReturn("No autorizado");

		assertThrows(UnauthorizedActionException.class, () -> {
			contactService.deleteContactById(1L);
		});
	}

}