package com.techforb.apiportalrecruiting.core.services.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.dtos.contactTypes.RequestContactTypeDTO;
import com.techforb.apiportalrecruiting.core.dtos.contactTypes.ResponseContactTypeDTO;
import com.techforb.apiportalrecruiting.core.entities.ContactType;
import com.techforb.apiportalrecruiting.core.repositories.ContactTypeRepository;
import com.techforb.apiportalrecruiting.core.services.ContactTypeService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ActiveProfiles("test")
@SpringBootTest
class ContactTypeServiceImplTest {

	@Autowired
	private ContactTypeService contactTypeService;

	@MockitoBean
	private ContactTypeRepository contactTypeRepository;

	@MockitoBean
	private LocalizedMessageService localizedMessageService;

	@MockitoBean
	private ModelMapper modelMapper;

	private final String CONTACT_NAME = "WHATSAPP";

	private ContactType contactType;
	private List<ContactType> contactTypes;
	private RequestContactTypeDTO requestContactTypeDTO;
	private ResponseContactTypeDTO responseContactTypeDTO;

	@BeforeEach
	void setUp() {
		contactType = new ContactType();
		contactType.setId(1L);
		contactType.setName(CONTACT_NAME);

		contactTypes = new ArrayList<>();
		contactTypes.add(contactType);

		requestContactTypeDTO = new RequestContactTypeDTO();
		requestContactTypeDTO.setName(CONTACT_NAME);

		responseContactTypeDTO = new ResponseContactTypeDTO();
		responseContactTypeDTO.setName(CONTACT_NAME);
		responseContactTypeDTO.setId(1L);
	}

	@Test
	void getContactTypeByName__Success() {
		when(contactTypeRepository.findByName(CONTACT_NAME)).thenReturn(contactType);

		ContactType result = contactTypeService.getContactTypeByName(CONTACT_NAME);

		assertNotNull(result);
		assertEquals(contactType.getName(), result.getName());
	}

	@Test
	void getContactTypeByName__Error() {
		when(contactTypeRepository.findByName(CONTACT_NAME)).thenReturn(null);
		when(localizedMessageService.getMessage("contact_type.not_found_by_name", CONTACT_NAME))
				.thenReturn("El tipo de Contacto no fue encontrado.");

		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
			contactTypeService.getContactTypeByName(CONTACT_NAME);
		});

		assertNotNull(exception);
		assertEquals("El tipo de Contacto no fue encontrado.", exception.getMessage());
	}


	@Test
	void getAllContactTypes() {
		when(contactTypeRepository.findAll()).thenReturn(contactTypes);
		when(modelMapper.map(contactType, ResponseContactTypeDTO.class)).thenReturn(responseContactTypeDTO);

		List<ResponseContactTypeDTO> result = contactTypeService.getAllContactTypes();

		assertNotNull(result);
		assertEquals(contactTypes.size(), result.size());
		assertEquals(contactTypes.get(0).getName(), result.get(0).getName());
	}

	@Test
	void getContactTypeById__Success() {
		when(contactTypeRepository.findById(1L)).thenReturn(Optional.of(contactType));
		when(modelMapper.map(contactType, ResponseContactTypeDTO.class)).thenReturn(responseContactTypeDTO);

		ResponseContactTypeDTO result = contactTypeService.getContactTypeById(1L);

		assertNotNull(result);
		assertEquals(contactType.getName(), result.getName());
	}

	@Test
	void getContactTypeById__Error() {
		when(contactTypeRepository.findById(10L)).thenReturn(Optional.empty());
		when(localizedMessageService.getMessage("contact_type.not_found_by_id",10L))
				.thenReturn("El tipo de Contacto no fue encontrado.");

		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
			contactTypeService.getContactTypeById(10L);
		});

		assertNotNull(exception);
		assertEquals("El tipo de Contacto no fue encontrado.", exception.getMessage());
	}

	@Test
	void getContactTypeEntityById__Success() {
		when(contactTypeRepository.findById(1L)).thenReturn(Optional.of(contactType));

		ContactType result = contactTypeService.getContactTypeEntityById(1L);

		assertNotNull(result);
		assertEquals(contactType.getName(), result.getName());
	}

	@Test
	void getContactTypeEntityById__Error() {
		when(contactTypeRepository.findById(10L)).thenReturn(Optional.empty());
		when(localizedMessageService.getMessage("contact_type.not_found_by_id",10L))
				.thenReturn("El tipo de Contacto no fue encontrado.");

		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
			contactTypeService.getContactTypeEntityById(10L);
		});

		assertNotNull(exception);
		assertEquals("El tipo de Contacto no fue encontrado.", exception.getMessage());
	}



	@Test
	void createContactType__Success() {
		when(modelMapper.map(requestContactTypeDTO, ContactType.class)).thenReturn(contactType);

		when(contactTypeRepository.save(contactType)).thenReturn(contactType);

		when(modelMapper.map(contactType, ResponseContactTypeDTO.class)).thenReturn(responseContactTypeDTO);

		ResponseContactTypeDTO result = contactTypeService.createContactType(requestContactTypeDTO);

		assertNotNull(result);
		assertEquals(contactType.getName(), result.getName());
	}

	@Test
	void updateContactType__Success() {
		when(contactTypeRepository.findById(1L)).thenReturn(Optional.of(contactType));
		when(contactTypeRepository.save(any(ContactType.class))).thenReturn(contactType);
		when(modelMapper.map(contactType, ResponseContactTypeDTO.class)).thenReturn(responseContactTypeDTO);

		ResponseContactTypeDTO result = contactTypeService.updateContactType(1L,requestContactTypeDTO);

		assertNotNull(result);
		assertEquals(requestContactTypeDTO.getName(), result.getName());
	}

	@Test
	void updateContactType_Error() {
		when(contactTypeRepository.findById(99L)).thenReturn(Optional.empty());
		when(localizedMessageService.getMessage("contact_type.not_found_by_id", 99L))
				.thenReturn("No se encontró el tipo de contacto");

		EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
				() -> contactTypeService.updateContactType(99L, requestContactTypeDTO));

		assertEquals("No se encontró el tipo de contacto", ex.getMessage());
	}

	@Test
	void deleteContactType_success() {
		when(contactTypeRepository.existsById(1L)).thenReturn(true);

		contactTypeService.deleteContactType(1L);

		verify(contactTypeRepository).deleteById(1L);
	}

	@Test
	void deleteContactType_notFound() {
		when(contactTypeRepository.existsById(99L)).thenReturn(false);
		when(localizedMessageService.getMessage("contact_type.not_found_by_id", 99L))
				.thenReturn("No se encontró el tipo de contacto");

		EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
				() -> contactTypeService.deleteContactType(99L));

		assertEquals("No se encontró el tipo de contacto", ex.getMessage());
	}
}
