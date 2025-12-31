package com.techforb.apiportalrecruiting.modules.portal.savedvacancies.mapper;

import com.techforb.apiportalrecruiting.core.dtos.LanguageDetailDTO;
import com.techforb.apiportalrecruiting.core.entities.DetailSkill;
import com.techforb.apiportalrecruiting.core.entities.Direction;
import com.techforb.apiportalrecruiting.core.entities.SavedVacancy;
import com.techforb.apiportalrecruiting.modules.portal.savedvacancies.dto.ResponseDetailSkillWithoutPriorityDTO;
import com.techforb.apiportalrecruiting.modules.portal.savedvacancies.dto.SavedVacancyDTO;
import com.techforb.apiportalrecruiting.modules.portal.savedvacancies.dto.SavedVacancyDetailsDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SavedVacancyMapper {

    public SavedVacancyDTO mapToSavedVacancyDTO(SavedVacancy savedVacancy) {
        if (savedVacancy == null) return null;
        
        return SavedVacancyDTO.builder()
                .id(savedVacancy.getId())
                .vacancy(mapToSavedVacancyDetailsDTO(savedVacancy))
                .savedDate(savedVacancy.getSavedDate())
                .build();
    }

    public SavedVacancyDetailsDTO mapToSavedVacancyDetailsDTO(SavedVacancy savedVacancy) {
        if (savedVacancy == null || savedVacancy.getVacancy() == null) return null;

        return SavedVacancyDetailsDTO.builder()
                .id(savedVacancy.getVacancy().getId())
                .role(savedVacancy.getVacancy().getRole())
                .description(savedVacancy.getVacancy().getDescription())
                .active(savedVacancy.getVacancy().getActive())
                .yearsExperienceRequired(savedVacancy.getVacancy().getYearsExperienceRequired())
                .nameCompany(savedVacancy.getVacancy().getCompany().getName())
                .skills(mapToResponseDetailSkillWithoutPriorityDTOList(savedVacancy.getVacancy().getDetailSkills()))
                .direction(buildCompleteDirection(savedVacancy.getVacancy().getDirection()))
                .workModality(savedVacancy.getVacancy().getWorkModality() != null ? savedVacancy.getVacancy().getWorkModality().toString() : "")
                .build();
    }

    public List<ResponseDetailSkillWithoutPriorityDTO> mapToResponseDetailSkillWithoutPriorityDTOList(List<DetailSkill> detailSkills) {
        if (detailSkills == null) {
            return List.of();
        }
        
        return detailSkills.stream()
                .map(this::mapToResponseDetailSkillWithoutPriorityDTO)
                .toList();
    }

    public ResponseDetailSkillWithoutPriorityDTO mapToResponseDetailSkillWithoutPriorityDTO(DetailSkill detailSkill) {
        if (detailSkill == null) return null;
        
        return ResponseDetailSkillWithoutPriorityDTO.builder()
                .id(detailSkill.getId())
                .descriptionSkill(detailSkill.getSkill() != null ? detailSkill.getSkill().getDescription() : null)
                .isObligatory(detailSkill.getIsObligatory())
                .language(detailSkill.getLanguage() != null ? mapToLanguageDetailDTO(detailSkill) : null)
                .yearsExperience(detailSkill.getYearsExperience())
                .build();
    }

    private LanguageDetailDTO mapToLanguageDetailDTO(DetailSkill detailSkill) {
        if (detailSkill.getLanguage() == null) return null;
        
        return new LanguageDetailDTO(
                detailSkill.getLanguage().getName(),
                detailSkill.getLanguage().getLanguageLevel()
        );
    }

        private String buildCompleteDirection(Direction direction) {
        if (direction == null) return null;
        
        StringBuilder fullDirection = new StringBuilder();
        
        if (direction.getCity() != null && direction.getCity().getName() != null) {
            fullDirection.append(direction.getCity().getName());
        }
        
        if (direction.getCity() != null && direction.getCity().getProvince() != null
            && direction.getCity().getProvince().getName() != null) {
            if (!fullDirection.isEmpty()) {
                fullDirection.append(", ");
            }
            fullDirection.append(direction.getCity().getProvince().getName());
        }
        
        if (direction.getCity() != null && direction.getCity().getProvince() != null
            && direction.getCity().getProvince().getCountry() != null
            && direction.getCity().getProvince().getCountry().getName() != null) {
            if (!fullDirection.isEmpty()) {
                fullDirection.append(", ");
            }
            fullDirection.append(direction.getCity().getProvince().getCountry().getName());
        }

            return !fullDirection.isEmpty() ? fullDirection.toString() : null;
    }
} 