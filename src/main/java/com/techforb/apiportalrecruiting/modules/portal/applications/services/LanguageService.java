package com.techforb.apiportalrecruiting.modules.portal.applications.services;

import com.techforb.apiportalrecruiting.core.entities.Language;
import com.techforb.apiportalrecruiting.core.entities.Person;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.LanguageDTO;

import java.util.List;

public interface LanguageService {
	Language findById(Long id);
	Language saveLanguage(Language newLanguage);
	List<LanguageDTO>listEnglishLevels();
	void saveLanguageForPerson(Long languageId, String email);
	void assignLanguageToPerson(Person person, Long languageId);
}
