package com.techforb.apiportalrecruiting.modules.portal.applications.services;

import com.techforb.apiportalrecruiting.core.entities.Skill;
import org.springframework.stereotype.Service;

@Service
public interface SkillService {
	Skill findByName(String name);
	Skill findById(Long id);
}
