package com.techforb.apiportalrecruiting.modules.portal.applications.services.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.entities.Language;
import com.techforb.apiportalrecruiting.core.entities.Person;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.core.repositories.UserRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.LanguageDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.LanguageRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.LanguageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {

	private final LanguageRepository languageRepository;
	private final UserRepository userRepository;
	private final LocalizedMessageService localizedMessageService;

    private static final String NOT_FOUND_LENGUAGE = "language.not_found";
    private static final String NOT_FOUND_USER = "user.not_found";
    private static final String NOT_FOUND_PERSON = "person.not_found";

	@Override
	public Language findById(Long id) {
		return languageRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(localizedMessageService.getMessage(NOT_FOUND_LENGUAGE)));
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
				.toList();
	}

	@Override
	public void saveLanguageForPerson(Long languageId, String email) {
		Language language = languageRepository.findById(languageId)
				.orElseThrow(() -> new EntityNotFoundException(localizedMessageService.getMessage(NOT_FOUND_LENGUAGE)));

		UserEntity user = userRepository.findByEmail(email)
				.orElseThrow(() -> new EntityNotFoundException(localizedMessageService.getMessage(NOT_FOUND_USER)));
		Person person = user.getPerson();
		if (person == null) {
			throw new EntityNotFoundException(localizedMessageService.getMessage(NOT_FOUND_PERSON));
		}

		List<Language> personLanguages = person.getLanguages();
		if (personLanguages == null) {
			personLanguages = new ArrayList<>();
		}

		personLanguages.removeIf(l ->
				l != null && "Ingles".equalsIgnoreCase(l.getName())
		);
		personLanguages.add(language);
		person.setLanguages(personLanguages);
	}

	@Override
	public void assignLanguageToPerson(Person person, Long languageId) {
		Language language = languageRepository.findById(languageId)
				.orElseThrow(() -> new EntityNotFoundException(
						localizedMessageService.getMessage(NOT_FOUND_LENGUAGE)
				));

		if (person.getLanguages() == null) {
			person.setLanguages(new ArrayList<>());
		}

		person.getLanguages().removeIf(l ->
				l != null && "Ingles".equalsIgnoreCase(l.getName())
		);
		person.getLanguages().add(language);
	}
}
