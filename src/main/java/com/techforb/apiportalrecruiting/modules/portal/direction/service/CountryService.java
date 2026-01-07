package com.techforb.apiportalrecruiting.modules.portal.direction.service;

import com.techforb.apiportalrecruiting.core.dtos.CountrySavedDTO;
import com.techforb.apiportalrecruiting.core.entities.Person;
import com.techforb.apiportalrecruiting.modules.portal.direction.dto.CountryItemDTO;

import java.util.List;

public interface CountryService {
	List<CountryItemDTO> listAll();
	CountrySavedDTO saveCountry(Long countryId, String email);
	void assignCountry(Person person, Long countryId);
}


