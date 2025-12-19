package com.techforb.apiportalrecruiting.modules.portal.applications.services.impl;

import com.techforb.apiportalrecruiting.core.entities.Skill;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.SkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ActiveProfiles("test")
@SpringBootTest
class SkillServiceImplTest {

	@MockitoBean
	private SkillRepository skillRepository;

	@Autowired
	private SkillServiceImpl skillService;

	private Skill mockSkill;

	@BeforeEach
	void setUp() {
		mockSkill = new Skill();
		mockSkill.setId(1L);
		mockSkill.setDescription("Java");
	}

	@Test
	void findByName_ShouldReturnSkill_WhenExists() {
		when(skillRepository.findByDescription("Java")).thenReturn(mockSkill);

		Skill result = skillService.findByName("Java");

		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals("Java", result.getDescription());
		verify(skillRepository, times(1)).findByDescription("Java");
	}

	@Test
	void findByName_ShouldReturnNull_WhenNotFound() {
		when(skillRepository.findByDescription("Python")).thenReturn(null);

		Skill result = skillService.findByName("Python");

		assertNull(result);
		verify(skillRepository, times(1)).findByDescription("Python");
	}

	@Test
	void findById_ShouldReturnSkill_WhenExists() {
		when(skillRepository.findById(1L)).thenReturn(Optional.of(mockSkill));

		Skill result = skillService.findById(1L);

		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals("Java", result.getDescription());
		verify(skillRepository, times(1)).findById(1L);
	}

	@Test
	void findById_ShouldReturnNull_WhenNotFound() {
		when(skillRepository.findById(2L)).thenReturn(Optional.empty());

		Exception exception = assertThrows(RuntimeException.class, () -> skillService.findById(2L));
		assertEquals("Habilidad no encontrada", exception.getMessage());
		verify(skillRepository, times(1)).findById(2L);
	}

}