package com.techforb.apiportalrecruiting.modules.portal.services.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.contactTypes.RequestContactTypeDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.contactTypes.ResponseContactTypeDTO;
import com.techforb.apiportalrecruiting.core.entities.ContactType;
import com.techforb.apiportalrecruiting.core.repositories.ContactTypeRepository;
import com.techforb.apiportalrecruiting.modules.portal.services.ContactTypeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactTypeServiceImpl implements ContactTypeService {

	private final ContactTypeRepository contactTypeRepository;
	private final LocalizedMessageService localizedMessageService;
	private final ModelMapper modelMapper;
    private static final String NOT_FOUND_BY_NAME_CODE = "contact_type.not_found_by_name";
    private static final String NOT_FOUND_BY_ID_CODE = "contact_type.not_found_by_id";

	@Override
	public ContactType getContactTypeByName(String name) {
		ContactType contactType = contactTypeRepository.findByName(name);
		if (contactType == null) {
			throw new EntityNotFoundException(localizedMessageService.getMessage(NOT_FOUND_BY_NAME_CODE, name));
		}
		return contactType;
	}

    @Override
    public List<ResponseContactTypeDTO> getAllContactTypes() {
        return contactTypeRepository.findAll()
                .stream()
                .map(contactType ->
                        modelMapper.map(contactType, ResponseContactTypeDTO.class)
                )
                .toList();
    }


    @Override
	public ResponseContactTypeDTO getContactTypeById(Long id) {
		return modelMapper.map(contactTypeRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(localizedMessageService.getMessage(NOT_FOUND_BY_ID_CODE, id))),ResponseContactTypeDTO.class);
	}

	@Override
	public ContactType getContactTypeEntityById(Long id) {
		return contactTypeRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(localizedMessageService.getMessage(NOT_FOUND_BY_ID_CODE, id)));
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
			throw new EntityNotFoundException(localizedMessageService.getMessage(NOT_FOUND_BY_ID_CODE, id));
		}
		contactTypeRepository.deleteById(id);
	}

	private ContactType findByIdOrThrow(Long id) {
		return contactTypeRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(
						localizedMessageService.getMessage(NOT_FOUND_BY_ID_CODE, id)
				));
	}
}
