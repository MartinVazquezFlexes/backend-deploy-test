package com.techforb.apiportalrecruiting.modules.portal.savedvacancies.service.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.entities.Person;
import com.techforb.apiportalrecruiting.core.entities.SavedVacancy;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.core.entities.Vacancy;
import com.techforb.apiportalrecruiting.core.exceptions.SavedVacancyAlreadySavedException;
import com.techforb.apiportalrecruiting.core.exceptions.SavedVacancyAuthenticationException;
import com.techforb.apiportalrecruiting.core.exceptions.SavedVacancyInactiveException;
import com.techforb.apiportalrecruiting.core.exceptions.SavedVacancyNotFoundException;
import com.techforb.apiportalrecruiting.core.repositories.VacancyRepository;
import com.techforb.apiportalrecruiting.core.security.CustomUserDetails;
import com.techforb.apiportalrecruiting.modules.portal.savedvacancies.dto.SavedVacancyDTO;
import com.techforb.apiportalrecruiting.modules.portal.savedvacancies.mapper.SavedVacancyMapper;
import com.techforb.apiportalrecruiting.modules.portal.savedvacancies.repository.SavedVacancyRepository;
import com.techforb.apiportalrecruiting.modules.portal.savedvacancies.service.SavedVacancyService;
import com.techforb.apiportalrecruiting.core.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SavedVacancyServiceImpl implements SavedVacancyService {

    private final SavedVacancyRepository savedVacancyRepository;
    private final VacancyRepository vacancyRepository;
    private final SavedVacancyMapper savedVacancyMapper;
    private final LocalizedMessageService localizedMessageService;

    @Override
    @Transactional(readOnly = true)
    public Page<SavedVacancyDTO> getSavedVacancies(Pageable pageable) {
        Person authenticatedPerson = getAuthenticatedPerson();

        Page<SavedVacancy> savedVacancies = savedVacancyRepository.findByPersonIdOrderBySavedDateDesc(authenticatedPerson.getId(), pageable);

        return savedVacancies.map(savedVacancyMapper::mapToSavedVacancyDTO);
    }

    @Override
    @Transactional
    public SavedVacancyDTO saveVacancy(Long vacancyId) {
        Person authenticatedPerson = getAuthenticatedPerson();

        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new SavedVacancyNotFoundException(
                        localizedMessageService.getMessage("saved.vacancy.not_found", vacancyId)));

        if (!Boolean.TRUE.equals(vacancy.getActive())) {
            throw new SavedVacancyInactiveException(
                    localizedMessageService.getMessage("saved.vacancy.inactive"));
        }

        if (savedVacancyRepository.existsByPersonAndVacancy(authenticatedPerson, vacancy)) {
            throw new SavedVacancyAlreadySavedException(
                    localizedMessageService.getMessage("saved.vacancy.already_saved"));
        }

        SavedVacancy savedVacancy = SavedVacancy.builder()
                .person(authenticatedPerson)
                .vacancy(vacancy)
                .build();

        SavedVacancy saved = savedVacancyRepository.save(savedVacancy);
        return savedVacancyMapper.mapToSavedVacancyDTO(saved);
    }

    private Person getAuthenticatedPerson() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SavedVacancyAuthenticationException(
                    localizedMessageService.getMessage("saved.vacancy.user_not_authenticated"));
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new SavedVacancyAuthenticationException(
                    localizedMessageService.getMessage("saved.vacancy.invalid_authentication"));
        }

        UserEntity user = ((CustomUserDetails) authentication.getPrincipal()).getUserEntity();

        if (user.getPerson() == null) {
            throw new SavedVacancyAuthenticationException(
                    localizedMessageService.getMessage("saved.vacancy.no_person_profile"));
        }
        return user.getPerson();
    }

}