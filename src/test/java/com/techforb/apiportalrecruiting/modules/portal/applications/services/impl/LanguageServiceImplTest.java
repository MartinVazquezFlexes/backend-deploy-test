package com.techforb.apiportalrecruiting.modules.portal.applications.services.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.entities.Language;
import com.techforb.apiportalrecruiting.core.entities.Person;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.modules.backoffice.user.UserRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.LanguageDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.LanguageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LanguageServiceImplTest {

	@Mock
	private LanguageRepository languageRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private LocalizedMessageService localizedMessageService;

	@InjectMocks
	private LanguageServiceImpl languageService;

	private Language mockLanguage;

	@BeforeEach
	void setUp() {
		mockLanguage = new Language();
		mockLanguage.setId(1L);
		mockLanguage.setName("Ingles");
		mockLanguage.setLanguageLevel("Intermedio");
	}

	@Test
	void findById_ShouldReturnLanguage_WhenExists() {
		when(languageRepository.findById(1L)).thenReturn(Optional.of(mockLanguage));

		Language result = languageService.findById(1L);

		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals("Ingles", result.getName());
		verify(languageRepository, times(1)).findById(1L);
	}

	@Test
	void findById_ShouldThrowRuntimeException_WhenNotFound() {
		when(languageRepository.findById(2L)).thenReturn(Optional.empty());
        when(localizedMessageService.getMessage("language.not_found")).thenReturn("Lenguaje no encontrado");
		Exception exception = assertThrows(RuntimeException.class, () -> languageService.findById(2L));

		assertEquals("Lenguaje no encontrado", exception.getMessage());
		verify(languageRepository, times(1)).findById(2L);
	}

	@Test
	void saveLanguage() {
		when(languageRepository.save(any(Language.class))).thenAnswer(invocation -> invocation.getArgument(0));
		Language toSave = new Language();
		toSave.setName("Ingles");
		toSave.setLanguageLevel("Basico");

		Language saved = languageService.saveLanguage(toSave);

		assertNotNull(saved);
		assertEquals("Ingles", saved.getName());
		assertEquals("Basico", saved.getLanguageLevel());
		verify(languageRepository, times(1)).save(toSave);
	}

	@Test
	void listEnglishLevels() {
		Language l1 = new Language(); l1.setId(1L); l1.setName("Ingles"); l1.setLanguageLevel("Basico");
		Language l2 = new Language(); l2.setId(2L); l2.setName("Ingles"); l2.setLanguageLevel("Intermedio");
		when(languageRepository.findByNameOrderByCustomLevel("Ingles")).thenReturn(java.util.List.of(l1, l2));

		var result = languageService.listEnglishLevels();
		assertNotNull(result);
		assertEquals(2, result.size());
		LanguageDTO first = result.get(0);
		assertEquals(1L, first.getId());
		assertEquals("Basico", first.getLanguageLevel());
		assertEquals("Ingles", first.getName());
		verify(languageRepository, times(1)).findByNameOrderByCustomLevel("Ingles");
	}

	@Test
	void saveLanguageForPerson_ShouldReplaceExistingEnglishLevel() {
		String email = "user@example.com";

		Language existingEnglish = new Language(); existingEnglish.setId(99L); existingEnglish.setName("Ingles"); existingEnglish.setLanguageLevel("Basico");
		Person person = new Person();
		person.setLanguages(new ArrayList<>(List.of(existingEnglish)));
		UserEntity user = new UserEntity();
		user.setPerson(person);

		when(languageRepository.findById(1L)).thenReturn(Optional.of(mockLanguage));
		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

		languageService.saveLanguageForPerson(1L, email);

		assertNotNull(person.getLanguages());
		assertEquals(1, person.getLanguages().size());
		assertEquals("Ingles", person.getLanguages().get(0).getName());
		assertEquals("Intermedio", person.getLanguages().get(0).getLanguageLevel());
	}

	@Test
	void saveLanguageForPerson_ShouldAddWhenNoExistingEnglish() {
		String email = "user@example.com";

		Person person = new Person();
		person.setLanguages(new ArrayList<>());
		UserEntity user = new UserEntity();
		user.setPerson(person);

		when(languageRepository.findById(1L)).thenReturn(Optional.of(mockLanguage));
		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

		languageService.saveLanguageForPerson(1L, email);

		assertNotNull(person.getLanguages());
		assertEquals(1, person.getLanguages().size());
		assertEquals("Ingles", person.getLanguages().get(0).getName());
	}

	@Test
	void saveLanguageForPerson_ShouldThrowWhenLanguageNotFound() {
		when(languageRepository.findById(10L)).thenReturn(Optional.empty());
		when(localizedMessageService.getMessage("language.not_found")).thenReturn("language.not_found");

		assertThrows(EntityNotFoundException.class, () -> languageService.saveLanguageForPerson(10L, "user@example.com"));
	}

	@Test
	void saveLanguageForPerson_ShouldThrowWhenUserNotFound() {
		when(languageRepository.findById(1L)).thenReturn(Optional.of(mockLanguage));
		when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
		when(localizedMessageService.getMessage("user.not_found")).thenReturn("user.not_found");

		assertThrows(RuntimeException.class, () -> languageService.saveLanguageForPerson(1L, "user@example.com"));
	}
}