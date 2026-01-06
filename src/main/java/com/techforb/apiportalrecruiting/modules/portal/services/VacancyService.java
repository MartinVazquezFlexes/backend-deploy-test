package com.techforb.apiportalrecruiting.modules.portal.services;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.vacancies.RequestFullVacancyDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.vacancies.VacancyDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.vacancies.VacancyRequestUpdateDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.vacancies.VacancyNotActiveDTO;
import com.techforb.apiportalrecruiting.core.entities.Vacancy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.vacancies.VacancyDetailsDTO;

/**
 * Service interface for managing vacancy-related operations.
 */
@Service
public interface VacancyService {
    /**
     * Retrieves active vacancies with associated language details.
     *
     * @param pageable Pagination information for the request
     * @return Page of VacancyDTO containing active vacancies with language details
     * @throws IllegalArgumentException if no active vacancies are found
     */
    Page<VacancyDTO> getVacanciesActiveWithLanguage(Pageable pageable);
    VacancyDTO updateVacancy(VacancyRequestUpdateDTO updatedVacancy, Long vacancyId);
    /**
     * Disables a specific vacancy based on its ID.
     *
     * @param vacancyId the unique identifier of the vacancy to disable.
     * @return a {@code VacancyNotActiveDTO} representing the disabled vacancy.
     */
    VacancyNotActiveDTO disableVacancy(Long vacancyId);
    VacancyDetailsDTO getDetailsVacancyById(Long id);
    Vacancy findById(Long vacancyId);
    VacancyDetailsDTO create(RequestFullVacancyDTO newVacancy);

}
