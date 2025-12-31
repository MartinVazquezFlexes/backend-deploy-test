package com.techforb.apiportalrecruiting.core.services;
import com.techforb.apiportalrecruiting.core.dtos.LanguageDetailDTO;
import com.techforb.apiportalrecruiting.core.dtos.ResponseDetailSkillDTO;
import com.techforb.apiportalrecruiting.core.entities.DetailSkill;
import com.techforb.apiportalrecruiting.core.entities.Language;
import com.techforb.apiportalrecruiting.core.entities.Vacancy;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.detailskill.RequestDetailSkillDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service interface for handling skill details.
 */
@Service
public interface DetailSkillService {
    /**
     * Finds the language details associated with a given vacancy ID.
     *
     * @param vacancyId the ID of the vacancy
     * @return a {@link LanguageDetailDTO} containing the language details
     */
    LanguageDetailDTO findLanguageByVancancyId(Long vacancyId);
    Language findDbLanguageByVacancyId(Long vacancyId);
    List<DetailSkill> findByVacancyId(Long vacancyId);
    List<ResponseDetailSkillDTO> getDetailSkillsByVacancyId(Long vacancyId);

    List<DetailSkill> createListDetails(Vacancy newVacancy,List<RequestDetailSkillDTO> detailSkillDTOList);
}
