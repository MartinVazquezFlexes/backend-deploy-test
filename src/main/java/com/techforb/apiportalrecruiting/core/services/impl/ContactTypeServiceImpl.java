package com.techforb.apiportalrecruiting.core.services.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.dtos.contactTypes.RequestContactTypeDTO;
import com.techforb.apiportalrecruiting.core.dtos.contactTypes.ResponseContactTypeDTO;
import com.techforb.apiportalrecruiting.core.entities.ContactType;
import com.techforb.apiportalrecruiting.core.repositories.ContactTypeRepository;
import com.techforb.apiportalrecruiting.core.services.ContactTypeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactTypeServiceImpl implements ContactTypeService {

	private final ContactTypeRepository contactTypeRepository;
	private final LocalizedMessageService localizedMessageService;
	private final ModelMapper modelMapper;

	@Override
	public ContactType getContactTypeByName(String name) {
		ContactType contactType = contactTypeRepository.findByName(name);
		if (contactType == null) {
			throw new EntityNotFoundException(localizedMessageService.getMessage("contact_type.not_found_by_name", name));
		}
		return contactType;
	}

	@Override
	public List<ResponseContactTypeDTO> getAllContactTypes() {
		return contactTypeRepository.findAll().stream().map(
				contactType -> modelMapper.map(contactType, ResponseContactTypeDTO.class)
		).collect(Collectors.toList());
	}

	@Override
	public ResponseContactTypeDTO getContactTypeById(Long id) {
		return modelMapper.map(contactTypeRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(localizedMessageService.getMessage("contact_type.not_found_by_id", id))),ResponseContactTypeDTO.class);
	}

	@Override
	public ContactType getContactTypeEntityById(Long id) {
		return contactTypeRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(localizedMessageService.getMessage("contact_type.not_found_by_id", id)));
	}

	@Override
	public ResponseContactTypeDTO createContactType(RequestContactTypeDTO requestContactTypeDTO) {
		ContactType contactType = modelMapper.map(requestContactTypeDTO, ContactType.class);
		return modelMapper.map(contactTypeRepository.save(contactType), ResponseContactTypeDTO.class);
	}

	@Override
	public ResponseContactTypeDTO updateContactType(Long id, RequestContactTypeDTO requestContactTypeDTO) {
		ContactType contactType = findByIdOrThrow(id);
		contactType.setName(requestContactTypeDTO.getName());
		return modelMapper.map(contactTypeRepository.save(contactType),ResponseContactTypeDTO.class);
	}

	@Override
	public void deleteContactType(Long id) {
		if (!contactTypeRepository.existsById(id)) {
			throw new EntityNotFoundException(localizedMessageService.getMessage("contact_type.not_found_by_id", id));
		}
		contactTypeRepository.deleteById(id);
	}

	private ContactType findByIdOrThrow(Long id) {
		return contactTypeRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(
						localizedMessageService.getMessage("contact_type.not_found_by_id", id)
				));
	}
}
