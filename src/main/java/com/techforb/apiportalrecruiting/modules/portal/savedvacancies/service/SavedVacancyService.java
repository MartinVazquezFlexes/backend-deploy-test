package com.techforb.apiportalrecruiting.modules.portal.savedvacancies.service;

import com.techforb.apiportalrecruiting.modules.portal.savedvacancies.dto.SavedVacancyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface SavedVacancyService {
    Page<SavedVacancyDTO> getSavedVacancies(Pageable pageable);
    SavedVacancyDTO saveVacancy(Long vacancyId);
}