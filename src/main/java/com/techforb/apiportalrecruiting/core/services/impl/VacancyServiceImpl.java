package com.techforb.apiportalrecruiting.core.services.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.config.mapper.ModelMapperUtils;
import com.techforb.apiportalrecruiting.core.dtos.*;
import com.techforb.apiportalrecruiting.core.entities.*;
import com.techforb.apiportalrecruiting.core.exceptions.UnauthorizedActionException;
import com.techforb.apiportalrecruiting.core.exceptions.VacancyNotActiveException;
import com.techforb.apiportalrecruiting.core.repositories.VacancyRepository;
import com.techforb.apiportalrecruiting.core.services.CompanyService;
import com.techforb.apiportalrecruiting.core.services.DetailSkillService;
import com.techforb.apiportalrecruiting.core.services.VacancyService;
import com.techforb.apiportalrecruiting.modules.backoffice.user.CustomUserDetails;
import com.techforb.apiportalrecruiting.modules.backoffice.user.UserService;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.LanguageService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of VacancyService for managing vacancy-related operations.
 */
@Service
@RequiredArgsConstructor
public class VacancyServiceImpl implements VacancyService {
    /**
     * Repository for vacancy-related database operations.
     */
    private final VacancyRepository vacancyRepository;
     /**
     * Service for retrieving skill-related details.
     */
    private final DetailSkillService detailSkillService;
    /**
     * Utility for object mapping.
     */
    private final ModelMapperUtils modelMapperUtils;
    /**
     * Service for managing internationalized messages.
     * Provides methods to retrieve localized messages based on the message code and the current or specified language.
     * It utilizes Spring's MessageSource to load messages from resource property files.
     */
    private final LocalizedMessageService localizedMessageService;
    private final LanguageService languageService;

    private final CompanyService companyService;
    private final UserService userService;

    @Override
    @Transactional
    public VacancyDetailsDTO getDetailsVacancyById(Long id) {


        Vacancy vacancy= this.findById(id);


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!vacancy.getActive()) {
            boolean allowAccess = false;

            if (authentication != null && authentication.isAuthenticated()) {
                boolean isRecruiterOrAdmin = authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_RECRUITER") || a.getAuthority().equals("ROLE_ADMIN"));
                if (isRecruiterOrAdmin) {
                    allowAccess = true;
                }
            }
            if (!allowAccess) {
                throw new VacancyNotActiveException(
                        localizedMessageService.getMessage("vacancy.not_active_args", id));
            }
        }

        List<DetailSkill> detailSkills = detailSkillService.findByVacancyId(id);
        detailSkills.forEach(detailSkill -> {
            Hibernate.initialize(detailSkill.getSkill());
            Hibernate.initialize(detailSkill.getLanguage());
        });

        VacancyDetailsDTO dto = modelMapperUtils.map(vacancy, VacancyDetailsDTO.class);
        dto.setSkills(modelMapperUtils.mapAll(detailSkills, ResponseDetailSkillDTO.class));

        return dto;
    }

    @Override
    public VacancyDetailsDTO create(RequestFullVacancyDTO newVacancyDto){
        UserEntity user=userService.getUserFromContext();
        if(user.getRoles().stream()
                .anyMatch(a -> a.getName().equals("RECRUITER"))){
            Vacancy newVacancy=this.createVacancyEntity(newVacancyDto);
            Vacancy vacancyResponse=this.vacancyRepository.save(newVacancy);
            VacancyDetailsDTO response= this.getDetailsVacancyById(vacancyResponse.getId());
            return response;
        }
        throw new UnauthorizedActionException(localizedMessageService.getMessage("user.without_permissions"));
    }
    private Vacancy createVacancyEntity(RequestFullVacancyDTO dto){
        Company company=companyService.findById(dto.getIdCompany());
        UserEntity recruiter=userService.findById(dto.getIdRecruiter());

        Vacancy newVacancy= new Vacancy();
        newVacancy.setCompany(company);
        newVacancy.setRecruiter(recruiter);
        newVacancy.setRole(dto.getRole());
        newVacancy.setDescription(dto.getDescription());
        newVacancy.setActive(dto.getActive());
        newVacancy.setYearsExperienceRequired(dto.getYearsExperienceRequired());
        newVacancy.setExpirationDate(dto.getExpirationDate());

        List<DetailSkill> detailSkills=detailSkillService.createListDetails(newVacancy,dto.getDetailsSkills());
        newVacancy.setDetailSkills(detailSkills);
        newVacancy.setCreationDate(LocalDateTime.now());
        return newVacancy;
    }

    /**
     * Retrieves a paginated list of active vacancies and maps them to DTOs,
     * including language details if available.
     *
     * @param pageable the pagination information
     * @return a page of {@link VacancyDTO} objects with language details
     * @throws EntityNotFoundException if no active vacancies are found
     */
    @Override
    public Page<VacancyDTO> getVacanciesActiveWithLanguage(Pageable pageable) {

        Page<Vacancy> vacanciesActives = this.vacancyRepository.findAllVacanciesWithPaginationActive(pageable);

        if (vacanciesActives.isEmpty()) {
            throw new EntityNotFoundException(localizedMessageService.getMessage("vacancy.active_not_found"));
        }

        return vacanciesActives.map(vacancy -> {
            VacancyDTO vacancyLanguageDTO = modelMapperUtils.map(vacancy, VacancyDTO.class);
            LanguageDetailDTO languageDetailDto = detailSkillService.findLanguageByVancancyId(vacancy.getId());
            if (languageDetailDto != null) {
                vacancyLanguageDTO.setLanguage(languageDetailDto);
            }
            return vacancyLanguageDTO;
        });
    }

    @Override
    public VacancyDTO updateVacancy(VacancyRequestUpdateDTO updatedVacancy, Long vacancyId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isRecruiterOrAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().contains("RECRUITER") || a.getAuthority().contains("ADMIN"));

        boolean isOwnerOfTheVacancy = Objects.equals(findById(vacancyId).getRecruiter().getId(), ((CustomUserDetails) authentication.getPrincipal()).getUserEntity().getId());

        if(!isRecruiterOrAdmin || !isOwnerOfTheVacancy) throw new VacancyNotActiveException(localizedMessageService.getMessage("user.without_permission"));

        Vacancy dbVacancy = findById(vacancyId);
        dbVacancy.setRole(updatedVacancy.getRole());
        dbVacancy.setDescription(updatedVacancy.getDescription());
        dbVacancy.setYearsExperienceRequired(updatedVacancy.getYearsExperienceRequired());

        Language dbLanguage = detailSkillService.findDbLanguageByVacancyId(vacancyId);
        dbLanguage.setLanguageLevel(updatedVacancy.getLanguageLevel());

        Language savedLanguage =  languageService.saveLanguage(dbLanguage);
        Vacancy savedVacancy =  vacancyRepository.save(dbVacancy);

        VacancyDTO vacancyLanguageDTO = modelMapperUtils.map(savedVacancy, VacancyDTO.class);
        LanguageDetailDTO languageDetailDto = modelMapperUtils.map(savedLanguage, LanguageDetailDTO.class);
        vacancyLanguageDTO.setLanguage(languageDetailDto);

        return vacancyLanguageDTO;
    }

    @Override
    public Vacancy findById(Long vacancyId) {
        return vacancyRepository.findById(vacancyId).orElseThrow(() -> new EntityNotFoundException(localizedMessageService.getMessage("vacancy.not_found_by_id")));
    }
    /**
     * Disables a vacancy if it is currently active.
     * If the vacancy is already disabled, an exception is thrown.
     *
     * @param vacancyId the unique identifier of the vacancy to disable.
     * @return a {@code VacancyNotActiveDTO} containing the ID and updated active status of the vacancy.
     * @throws IllegalArgumentException if the vacancy is already disabled.
     */
    @Override
    public VacancyNotActiveDTO disableVacancy(Long vacancyId) {
        VacancyNotActiveDTO vacancyAlreadyDisabled=new VacancyNotActiveDTO();
        Vacancy vacancyToDisable=this.findById(vacancyId);
        vacancyAlreadyDisabled.setId(vacancyId);
        if (vacancyToDisable.getActive()){
            vacancyToDisable.setActive(false);
            this.vacancyRepository.save(vacancyToDisable);
            vacancyAlreadyDisabled.setActive(vacancyToDisable.getActive());
            return vacancyAlreadyDisabled;
        }throw new IllegalArgumentException(localizedMessageService.getMessage("vacancy.disable"));
    }
}
