package com.techforb.apiportalrecruiting.modules.portal.applications.services.impl;

import com.techforb.apiportalrecruiting.core.entities.Skill;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.SkillRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

	private final SkillRepository skillRepository;

	@Override
	public Skill findByName(String name) {
		return skillRepository.findByDescription(name);
	}

	@Override
	public Skill findById(Long id) {
		return skillRepository.findById(id).orElseThrow(() -> new RuntimeException("Habilidad no encontrada"));
	}
}
