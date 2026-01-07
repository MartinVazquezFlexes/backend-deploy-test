package com.techforb.apiportalrecruiting.modules.portal.applications.services.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.entities.Skill;
import com.techforb.apiportalrecruiting.core.entities.Person;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.core.services.UserService;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.PersonRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.SkillRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.SkillService;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.SkillItemDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillServiceImpl implements SkillService {

	private final SkillRepository skillRepository;
	private final UserService userService;
	private final PersonRepository personRepository;
	private final LocalizedMessageService localizedMessageService;

	@Override
	public Skill findByName(String name) {
		return skillRepository.findByDescription(name);
	}

	@Override
	public Skill findById(Long id) {
		return skillRepository.findById(id).orElseThrow(() -> new RuntimeException("Habilidad no encontrada"));
	}
	
	@Override
	public List<SkillItemDTO> getAll() {
		try {
			List<Skill> skills = skillRepository.findAll(Sort.by(Sort.Direction.ASC, "description"));
			return skills.stream()
				.map(skill -> SkillItemDTO.builder()
					.id(skill.getId())
					.name(skill.getDescription())
					.build())
				.collect(Collectors.toList());
		} catch (DataAccessException e) {
			log.error("Error fetching all skills", e);
			throw new RuntimeException(localizedMessageService.getMessage("error.fetching.skills"), e);
		}
	}
	
	@Override
	@Transactional
	public void updatePersonSkills(String email, List<Long> skillIds) {
		Optional<UserEntity> user = userService.findByEmail(email);
		
		Person person = user.get().getPerson();
		if (person == null) {
			throw new EntityNotFoundException(localizedMessageService.getMessage("person.not_found"));
		}
		
		try {
			List<Skill> skills = skillIds.stream()
				.map(this::findById)
				.collect(Collectors.toList());
			
			person.setSkills(skills);
			personRepository.save(person);
		} catch (DataAccessException e) {
			log.error("Error updating skills for person", e);
			throw new RuntimeException(localizedMessageService.getMessage("error.updating.skills"), e);
		}
	}

	@Override
	public void assignPersonSkills(Person person, List<Long> skillIds) {
		List<Skill> skills = skillIds.stream()
				.map(this::findById)
				.collect(Collectors.toList());

		person.setSkills(skills);
	}

}
