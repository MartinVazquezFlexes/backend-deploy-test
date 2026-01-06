package com.techforb.apiportalrecruiting.modules.portal.applications.services;

import com.techforb.apiportalrecruiting.core.entities.Skill;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.SkillItemDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SkillService {
	Skill findByName(String name);
	Skill findById(Long id);
	
	void updatePersonSkills(String email, List<Long> skillIds);
	
	List<SkillItemDTO> getAll();
}
