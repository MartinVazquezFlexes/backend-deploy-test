package com.techforb.apiportalrecruiting.modules.portal.applications.dtos;

import com.techforb.apiportalrecruiting.core.dtos.LanguageDetailDTO;
import com.techforb.apiportalrecruiting.core.dtos.ResponseDetailSkillDTO;
import com.techforb.apiportalrecruiting.core.entities.Application;
import com.techforb.apiportalrecruiting.core.entities.Cv;
import com.techforb.apiportalrecruiting.core.entities.DetailSkill;
import com.techforb.apiportalrecruiting.core.entities.Vacancy;
import com.techforb.apiportalrecruiting.core.security.cloudinary.CloudinaryService;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application.ResponseApplicationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ApplicationMapper {
	private final CloudinaryService cloudinaryService;

	public ApplicationDTO mapToDTO(Application application) {
		return ApplicationDTO.builder()
				.id(application.getId())
				.comments(application.getComments())
				.applicationDate(application.getApplicationDate())
				.applicationState(application.getApplicationState())
				.vacancy(mapToVacancyDTO(application.getVacancy()))
				.cv(mapToCvDTO(application.getCv()))
				.detailSkill(mapToListResponseDetailSkillDTO(application.getDetailSkills()))
				.language(mapToListLanguageDTO(application.getDetailSkills()))
				.build();
	}

	public static VacancyDTO mapToVacancyDTO(Vacancy vacancy) {
		if (vacancy == null) return null;
		return new VacancyDTO(
				vacancy.getId(),
				vacancy.getCompany().getName(),
				vacancy.getRole(),
				vacancy.getDescription(),
				vacancy.getActive(),
				vacancy.getYearsExperienceRequired()
		);
	}

	public CvDTO mapToCvDTO(Cv cv) {
		if (cv == null) return null;
		return new CvDTO(
				cv.getId(),
				cloudinaryService.generateSignedUrl(cv.getPublicId(), cv.getVersion()),
				cv.getName()
		);
	}

	public static List<ResponseDetailSkillDTO> mapToListResponseDetailSkillDTO(List<DetailSkill> detailSkills) {
        if (detailSkills == null || detailSkills.isEmpty()) {
            return List.of();
        }
		return detailSkills.stream()
				.map(detailSkill -> ResponseDetailSkillDTO.builder()
						.id(detailSkill.getId())
						.descriptionSkill(detailSkill.getSkill() != null ? detailSkill.getSkill().getDescription() : null)
						.isObligatory(detailSkill.getIsObligatory())
						.language(detailSkill.getLanguage() != null ? mapToLanguageDTO(detailSkill) : null)
						.priority(detailSkill.getPriority())
						.yearsExperience(detailSkill.getYearsExperience())
						.build()
				)
				.collect(Collectors.toList());
	}

    public static List<LanguageDTO> mapToListLanguageDTO(List<DetailSkill> detailSkills) {

        if (detailSkills == null || detailSkills.isEmpty()) {
            return List.of();
        }

        return detailSkills.stream()
                .filter(detailSkill -> detailSkill.getLanguage() != null)
                .map(detailSkill -> new LanguageDTO(
                        detailSkill.getLanguage().getId(),
                        detailSkill.getLanguage().getName(),
                        detailSkill.getLanguage().getLanguageLevel()
                ))
                .toList();
    }


    public static LanguageDetailDTO mapToLanguageDTO(DetailSkill detailSkill) {
		return new LanguageDetailDTO(
				detailSkill.getLanguage().getName(),
				detailSkill.getLanguage().getLanguageLevel()
		);
	}

	public ResponseApplicationDTO mapToResponseApplicationDTO(Application application) {
		if (application == null) return null;
		return new ResponseApplicationDTO(
				application.getId(),
				application.getPerson().getUser().getEmail(),
				application.getComments(),
				cloudinaryService.generateSignedUrl(application.getCv().getPublicId(),application.getCv().getVersion()),
				application.getApplicationDate(),
				application.getUpdateDate(),
				application.getApplicationState(),
				mapToListResponseDetailSkillDTO(application.getDetailSkills())
		);
	}
}
