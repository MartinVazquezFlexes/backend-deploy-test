package com.techforb.apiportalrecruiting.core.services.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.dtos.LanguageDetailDTO;
import com.techforb.apiportalrecruiting.core.dtos.ResponseDetailSkillDTO;
import com.techforb.apiportalrecruiting.core.entities.DetailSkill;
import com.techforb.apiportalrecruiting.core.entities.Language;
import com.techforb.apiportalrecruiting.core.entities.Skill;
import com.techforb.apiportalrecruiting.core.entities.Vacancy;
import com.techforb.apiportalrecruiting.core.repositories.DetailSkillRepository;
import com.techforb.apiportalrecruiting.core.services.DetailSkillService;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.ApplicationMapper;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.detailSkill.RequestDetailSkillDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.LanguageService;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DetailSkillServiceImpl implements DetailSkillService {

	/**
	 * Repository for detail skill-related database operations.
	 */
	private final DetailSkillRepository detailSkillRepository;
	/**
	 * Service for managing internationalized messages.
	 * Provides methods to retrieve localized messages based on the message code and the current or specified language.
	 * It utilizes Spring's MessageSource to load messages from resource property files.
	 */
	private final LocalizedMessageService localizedMessageService;

	private final SkillService skillService;
	private final LanguageService languageService;

	@Override
	public List<DetailSkill> findByVacancyId(Long vacancyId) {
		return detailSkillRepository.findByVacancyIdWithRelations(vacancyId);
	}


	/**
	 * Retrieves language details associated with a specific vacancy ID.
	 * If no matching records are found or the language information is null,
	 * this method returns {@code null}.
	 *
	 * @param vacancyId the ID of the vacancy to search for
	 * @return a {@link LanguageDetailDTO} containing language details, or {@code null} if no data is found
	 */
	@Override
	public LanguageDetailDTO findLanguageByVancancyId(Long vacancyId) {
		List<DetailSkill> detailSkills = detailSkillRepository.findLanguagesByVancancyId(vacancyId);

		if (detailSkills.isEmpty()) {
			return null;
		}

		DetailSkill detailSkill = detailSkills.get(0);

		if (detailSkill.getLanguage() == null) {
			return null;
		}

		LanguageDetailDTO languageDetailDTO = new LanguageDetailDTO();
		languageDetailDTO.setName(detailSkill.getLanguage().getName());
		languageDetailDTO.setLanguageLevel(detailSkill.getLanguage().getLanguageLevel());
		return languageDetailDTO;
	}

	@Override
	public Language findDbLanguageByVacancyId(Long vacancyId) {
		List<DetailSkill> detailSkills = detailSkillRepository.findLanguagesByVancancyId(vacancyId);

		if (detailSkills.isEmpty()) {
			return null;
		}

		DetailSkill detailSkill = detailSkills.get(0);

		if (detailSkill.getLanguage() == null) {
			return null;
		}

		return detailSkill.getLanguage();
	}

	@Override
	public List<ResponseDetailSkillDTO> getDetailSkillsByVacancyId(Long vacancyId) {
		List<DetailSkill> detailSkills = detailSkillRepository.getDetailSkillByVacancy_Id(vacancyId);
		return ApplicationMapper.mapToListResponseDetailSkillDTO(detailSkills);
	}

	@Override
	public List<DetailSkill> createListDetails(Vacancy newVacancy, List<RequestDetailSkillDTO> detailSkillDTOList) {
		List<DetailSkill> reponse= new ArrayList();
		for (RequestDetailSkillDTO detaildto : detailSkillDTOList){
			DetailSkill detailEntity= new DetailSkill();
			detailEntity.setVacancy(newVacancy);
			detailEntity.setIsObligatory(detaildto.getIsObligatory());
			detailEntity.setPriority(detaildto.getPriority());
			detailEntity.setYearsExperience(detaildto.getYearsExperience());
			if(detaildto.getSkillId()!=null){
				Skill skill=this.skillService.findById(detaildto.getSkillId());
				detailEntity.setSkill(skill);
			} else if (detaildto.getLanguageId()!=null) {
				Language language=this.languageService.findById(detaildto.getLanguageId());
				detailEntity.setLanguage(language);
			}
			reponse.add(detailEntity);
		}
		return reponse;
	}



}
