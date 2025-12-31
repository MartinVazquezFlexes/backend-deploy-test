package com.techforb.apiportalrecruiting.modules.portal.applications.services.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.dtos.contacts.ReqResContactDTO;
import com.techforb.apiportalrecruiting.core.entities.City;
import com.techforb.apiportalrecruiting.core.entities.Contact;
import com.techforb.apiportalrecruiting.core.entities.ContactType;
import com.techforb.apiportalrecruiting.core.entities.Country;
import com.techforb.apiportalrecruiting.core.entities.Direction;
import com.techforb.apiportalrecruiting.core.entities.Person;
import com.techforb.apiportalrecruiting.core.entities.Province;
import com.techforb.apiportalrecruiting.core.entities.Skill;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.core.entities.ZipCode;
import com.techforb.apiportalrecruiting.core.repositories.ContactRepository;
import com.techforb.apiportalrecruiting.core.repositories.ContactTypeRepository;
import com.techforb.apiportalrecruiting.core.services.ContactTypeService;
import com.techforb.apiportalrecruiting.modules.backoffice.user.UserRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.SkillDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.PersonRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.SkillRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.PersonService;
import com.techforb.apiportalrecruiting.modules.portal.direction.repository.CityRepository;
import com.techforb.apiportalrecruiting.modules.portal.direction.repository.CountryRepository;
import com.techforb.apiportalrecruiting.modules.portal.direction.repository.DirectionRepository;
import com.techforb.apiportalrecruiting.modules.portal.direction.repository.ProvinceRepository;
import com.techforb.apiportalrecruiting.modules.portal.direction.repository.ZipCodeRepository;
import com.techforb.apiportalrecruiting.modules.portal.identification.dto.IdentificationDTO;
import com.techforb.apiportalrecruiting.modules.portal.identification.dto.IdentificationTypeDTO;
import com.techforb.apiportalrecruiting.core.entities.Identification;
import com.techforb.apiportalrecruiting.core.entities.IdentificationType;
import com.techforb.apiportalrecruiting.modules.portal.identification.repository.IdentificationRepository;
import com.techforb.apiportalrecruiting.modules.portal.identification.repository.IdentificationTypeRepository;
import com.techforb.apiportalrecruiting.modules.portal.person.dto.PersonRequestDTO;
import com.techforb.apiportalrecruiting.modules.portal.person.dto.PersonResponseDTO;
import com.techforb.apiportalrecruiting.modules.portal.person.dto.PersonMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

	private final PersonRepository personRepository;
	private final UserRepository userRepository;
	private final ContactRepository contactRepository;
	private final ContactTypeRepository contactTypeRepository;
	private final IdentificationRepository identificationRepository;
	private final IdentificationTypeRepository identificationTypeRepository;
	private final DirectionRepository directionRepository;
	private final CityRepository cityRepository;
	private final ProvinceRepository provinceRepository;
	private final CountryRepository countryRepository;
	private final ZipCodeRepository zipCodeRepository;
	private final SkillRepository skillRepository;
	private final PersonMapper personMapper;
	private final LocalizedMessageService localizedMessageService;
	private final ContactTypeService contactTypeService;

	private static final String PERSON_CODE_NOT_FOUND = "person.not_found";
    private static  final String USER_CODE_NOT_FOUND = "user.not_found";
    private static  final String DIRECTION_BY_ID_NOT_FOUND = "direction.not_found_by_id";

	@Override
	public Person getPersonById(Long id) {
		return personRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(localizedMessageService.getMessage(PERSON_CODE_NOT_FOUND)));
	}

	@Override
	public PersonResponseDTO getPersonByIdDTO(Long id) {
		Person person = personRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(localizedMessageService.getMessage(PERSON_CODE_NOT_FOUND)));
		return personMapper.mapToPersonDTO(person);
	}

	@Override
	@Transactional
	public PersonResponseDTO updatePerson(Long id, PersonRequestDTO dto) {
		Person person = personRepository.findById(id).
                orElseThrow(()-> new EntityNotFoundException(localizedMessageService.getMessage(PERSON_CODE_NOT_FOUND)));

		Optional<UserEntity> user= userRepository.findByEmail(dto.getEmail());
        if(user.isEmpty()){
            throw new EntityNotFoundException(localizedMessageService.getMessage(USER_CODE_NOT_FOUND));
        }
		person.setFirstName(dto.getFirstName());
		person.setLastName(dto.getLastName());

		person.setDateBirth(dto.getDateBirth().atStartOfDay());

		Direction direction = updateDirection(dto);
		person.setDirection(direction);

		List<Skill> finalSkillList = updateSkillList(dto.getSkillDTO());
		person.setSkills(finalSkillList);


		List<Contact> finalContactList = updateContactList(dto, person);
		person.setContacts(finalContactList);

		List<Identification> finalIdentificationList = updateIdentificationList(dto, person);
		person.setIdentifications(finalIdentificationList);

		personRepository.save(person);

		return personMapper.mapToPersonDTO(person);
	}

	private Direction updateDirection(PersonRequestDTO dto) {
		Direction direction = directionRepository.findById(dto.getDirectionId())
				.orElseThrow(() -> new EntityNotFoundException(localizedMessageService.getMessage(DIRECTION_BY_ID_NOT_FOUND, dto.getDirectionId())));

		Country country = countryRepository.findByNameIgnoreCase(dto.getCountry())
				.orElseGet(() -> countryRepository.save(Country.builder().name(dto.getCountry()).build()));

		Province province = provinceRepository.findByNameIgnoreCase(dto.getProvince())
				.filter(p -> p.getCountry().getName().equalsIgnoreCase(dto.getCountry()))
				.orElseGet(() -> provinceRepository.save(Province.builder().name(dto.getProvince()).country(country).build()));

		City city = cityRepository.findByNameIgnoreCase(dto.getCity())
				.filter(c -> c.getProvince().getName().equalsIgnoreCase(dto.getProvince()))
				.orElseGet(() -> cityRepository.save(City.builder().name(dto.getCity()).province(province).build()));

		ZipCode zipCode = zipCodeRepository.findByName(dto.getZipCode())
				.orElseGet(() -> zipCodeRepository.save(ZipCode.builder().name(dto.getZipCode()).build()));

		direction.setDescription(dto.getDirectionDescription());
		direction.setCity(city);
		direction.setZipCode(zipCode);

		directionRepository.save(direction);
		return direction;
	}

	private List<Contact> updateContactList(PersonRequestDTO dto, Person person) {
		List<Contact> existingList = person.getContacts();
		List<ReqResContactDTO> dtoList = dto.getContactDTOS();

		Map<Long, Contact> existingMap = existingList.stream()
				.collect(Collectors.toMap(Contact::getId, c -> c));

		List<Contact> finalList = new ArrayList<>();

		for (ReqResContactDTO contactDTO : dtoList) {
			ContactType type;
			try {
				type = contactTypeService.getContactTypeByName(contactDTO.getContactType());
			} catch (EntityNotFoundException e) {
				type = contactTypeRepository.save(ContactType.builder()
						.name(contactDTO.getContactType()).build());
			}

			if (contactDTO.getId() != null && existingMap.containsKey(contactDTO.getId())) {
				Contact existing = existingMap.get(contactDTO.getId());
				existing.setValue(contactDTO.getValue());
				existing.setLabel(contactDTO.getLabel());
				existing.setContactType(type);
				finalList.add(existing);
				existingMap.remove(contactDTO.getId());
			} else {
				Contact newContact = Contact.builder()
						.value(contactDTO.getValue())
						.label(contactDTO.getLabel())
						.contactType(type)
						.person(person)
						.build();
				contactRepository.save(newContact);
				finalList.add(newContact);
			}
		}

		for (Contact toDelete : existingMap.values()) {
			contactRepository.delete(toDelete);
		}

		return finalList;
	}

	private List<Identification> updateIdentificationList(PersonRequestDTO dto, Person person) {
		List<Identification> existingList = person.getIdentifications();
		List<IdentificationDTO> dtoList = dto.getIdentificationDTO();

		Map<Long, Identification> existingMap = existingList.stream()
				.collect(Collectors.toMap(Identification::getId, i -> i));

		List<Identification> finalList = new ArrayList<>();

		for (IdentificationDTO identificationDTO : dtoList) {
			IdentificationTypeDTO typeDTO = identificationDTO.getIdentificationTypeDTO();

			IdentificationType type = identificationTypeRepository.findById(typeDTO.getId())
					.orElseGet(() -> identificationTypeRepository.save(
							IdentificationType.builder().description(typeDTO.getDescription()).build()
					));

			if (identificationDTO.getId() != null && existingMap.containsKey(identificationDTO.getId())) {
				Identification existing = existingMap.get(identificationDTO.getId());
				existing.setDescription(identificationDTO.getDescription());
				existing.setDocumentType(type);
				finalList.add(existing);
				existingMap.remove(identificationDTO.getId());
			} else {
				Identification newIdentification = Identification.builder()
						.description(identificationDTO.getDescription())
						.documentType(type)
						.person(person)
						.build();
				identificationRepository.save(newIdentification);
				finalList.add(newIdentification);
			}
		}

		for (Identification toDelete : existingMap.values()) {
			identificationRepository.delete(toDelete);
		}

		return finalList;
	}

	private List<Skill> updateSkillList(List<SkillDTO> dto) {

		if (dto == null || dto.isEmpty()) {
			return new ArrayList<>();
		}

		List<Skill> updatedSkills = new ArrayList<>();

		for (SkillDTO skillDTO : dto) {
			if (skillDTO.getId() == null) {
				throw new IllegalArgumentException(localizedMessageService.getMessage("skill.required_id"));
			}

			Skill existingSkill = skillRepository.findById(skillDTO.getId())
					.orElseThrow(() -> new EntityNotFoundException(localizedMessageService.getMessage("skill.not_found_by_id", skillDTO.getId())));
			updatedSkills.add(existingSkill);
		}

		return updatedSkills;
	}

	public Person createPerson(UserEntity user) {
		Person newPerson = new Person();
		newPerson.setUser(user);
		return personRepository.save(newPerson);
	}

}
