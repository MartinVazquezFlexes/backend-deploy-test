package com.techforb.apiportalrecruiting.modules.portal.applications.services.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.entities.Person;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.core.entities.Language;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.LanguageDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.LanguageRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.LanguageService;
import com.techforb.apiportalrecruiting.modules.backoffice.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {

	private final LanguageRepository languageRepository;
	private final UserRepository userRepository;
	private final LocalizedMessageService localizedMessageService;

	@Override
	public Language findById(Long id) {
		return languageRepository.findById(id).orElseThrow(() -> new RuntimeException("Lenguaje no encontrado"));
	}

	@Override
	public Language saveLanguage(Language newLanguage) {
		return languageRepository.save(newLanguage);
	}

	@Override
	public List<LanguageDTO> listEnglishLevels() {
		return languageRepository.findByNameOrderByCustomLevel("Ingles")
				.stream()
				.map(l -> new LanguageDTO(l.getId(), l.getLanguageLevel(), l.getName()))
				.collect(Collectors.toList());
	}

	@Override
	public void saveLanguageForPerson(Long languageId, String email) {
		Language language = languageRepository.findById(languageId)
				.orElseThrow(() -> new RuntimeException(localizedMessageService.getMessage("language.not_found")));

		UserEntity user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException(localizedMessageService.getMessage("user.not_found")));
		Person person = user.getPerson();
		if (person == null) {
			throw new RuntimeException(localizedMessageService.getMessage("person.not_found"));
		}

		List<Language> personLanguages = person.getLanguages();
		if (personLanguages == null) {
			personLanguages = new ArrayList<>();
		}

		personLanguages.removeIf(l -> l.getName() != null && l.getName().equalsIgnoreCase("Ingles"));
		personLanguages.add(language);
		person.setLanguages(personLanguages);
	}
}
