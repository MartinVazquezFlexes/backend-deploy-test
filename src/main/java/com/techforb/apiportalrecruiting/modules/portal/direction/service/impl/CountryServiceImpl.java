package com.techforb.apiportalrecruiting.modules.portal.direction.service.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.dtos.CountrySavedDTO;
import com.techforb.apiportalrecruiting.core.entities.Country;
import com.techforb.apiportalrecruiting.core.entities.Person;
import com.techforb.apiportalrecruiting.core.entities.RoleFunctional;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.modules.backoffice.user.UserRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.PersonRepository;
import com.techforb.apiportalrecruiting.modules.portal.direction.dto.CountryItemDTO;
import com.techforb.apiportalrecruiting.modules.portal.direction.repository.CountryRepository;
import com.techforb.apiportalrecruiting.modules.portal.direction.service.CountryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CountryServiceImpl implements CountryService {

	private final CountryRepository countryRepository;
	private final UserRepository userRepository;
	private final LocalizedMessageService localizedMessageService;
	private final PersonRepository personRepository;

	@Override
	public List<CountryItemDTO> listAll() {
		List<Country> countries = countryRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
		return countries.stream()
				.map(c -> CountryItemDTO.builder().id(c.getId()).name(c.getName()).build())
				.collect(Collectors.toList());
	}

	@Override
	public CountrySavedDTO saveCountry(Long countryId, String email) {
			UserEntity user = userRepository.findByEmail(email)
					.orElseThrow(() -> new RuntimeException(localizedMessageService.getMessage("user.not_found")));

			Person person = user.getPerson();
			if (person == null) {
				throw new RuntimeException(localizedMessageService.getMessage("person.not_found"));
			}

			Country country = countryRepository.findById(countryId)
					.orElseThrow(() -> new RuntimeException(localizedMessageService.getMessage("country.not_found", countryId)));

			try {

				person.setCountryResidence(country);
				personRepository.save(person);
				CountrySavedDTO countrySavedDTO = CountrySavedDTO.builder()
						.id(country.getId())
						.name(country.getName())
						.build();
				return countrySavedDTO;
			} catch (DataAccessException e) {
				log.error("Error saving country  for person", e);
				throw new RuntimeException(localizedMessageService.getMessage("error.saving.country"), e);
			}
	}
}


