package com.techforb.apiportalrecruiting.modules.portal.applications.services.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.entities.*;
import com.techforb.apiportalrecruiting.core.exceptions.AlreadyAssignedCvException;
import com.techforb.apiportalrecruiting.core.exceptions.ApplicationClosedException;
import com.techforb.apiportalrecruiting.core.exceptions.CvNotOwnedException;
import com.techforb.apiportalrecruiting.core.exceptions.UnauthorizedActionException;
import com.techforb.apiportalrecruiting.core.services.VacancyService;
import com.techforb.apiportalrecruiting.modules.backoffice.user.UserService;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.ApplicationMapper;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application.RequestApplicationDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application.RequestChangeCvApplicationDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application.ResponseApplicationDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.ApplicationRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.owasp.encoder.Encode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

	private final ApplicationRepository applicationRepository;
	private final CvService cvService;
	private final VacancyService vacancyService;
	private final PersonService personService;
	private final SkillService skillService;
	private final LanguageService languageService;
	private final LocalizedMessageService localizedMessageService;
	private final UserService userService;
	private final ApplicationMapper applicationMapper;

	String[] dangerousPatterns = {"<script>", "</script>", "DROP", "DELETE", "SELECT", "INSERT", "UPDATE", "ALTER", "<iframe>", "javascript:"};

	@Override
	@Transactional
	public ResponseApplicationDTO applyVacancy(RequestApplicationDTO requestApplicationDTO, MultipartFile cvFile) {

		if (!existsApplicationInVacancy(requestApplicationDTO.getVacancyId(), requestApplicationDTO.getPersonId())) {
			throw new IllegalStateException(localizedMessageService.getMessage("application.postulant_already_applied"));
		}

		if (!validateComments(requestApplicationDTO.getComments())) {
			throw new IllegalStateException(localizedMessageService.getMessage("application.invalid_comments"));
		}

		Person person = personService.getPersonById(requestApplicationDTO.getPersonId());
		Application application = createApplicationEntity(requestApplicationDTO, person);

		String paramSubFolder = "/vacancy_" + requestApplicationDTO.getVacancyId();
		Cv cv = cvService.uploadCv(cvFile, person, paramSubFolder, false);
		application.setCv(cv);

		if (requestApplicationDTO.getRequestDetailSkillDTOS() != null) {
			List<DetailSkill> detailSkills = mapDetailSkills(requestApplicationDTO, application);
			application.setDetailSkills(detailSkills);
		}

		application = applicationRepository.save(application);
		return applicationMapper.mapToResponseApplicationDTO(application);
	}

	@Override
	public Application findById(Long applicationId) {
		return applicationRepository.findById(applicationId)
				.orElseThrow(() -> new EntityNotFoundException(localizedMessageService.getMessage("application.not_found_by_id") + applicationId));
	}

	@Override
	@Transactional
	public ResponseApplicationDTO changeCvApplication(RequestChangeCvApplicationDTO requestChangeCvApplicationDTO) {

		Application application = findById(requestChangeCvApplicationDTO.getApplicationId());

		UserEntity user = userService.getUserFromContext();
		if (!user.getId().equals(application.getPerson().getUser().getId())) {
			throw new UnauthorizedActionException(localizedMessageService.getMessage("user.not_authenticated"));
		}

		if (application.getApplicationState() == ApplicationState.FINISHED ||
				application.getApplicationState() == ApplicationState.CANCELED) {
			throw new ApplicationClosedException(localizedMessageService.getMessage("application.modify.invalid_state"));
		}

		if (application.getVacancy().getExpirationDate().isBefore(LocalDateTime.now())) {
			throw new ApplicationClosedException(localizedMessageService.getMessage("application.vacancy.invalid_date"));
		}

		Cv cv = cvService.findCvById(requestChangeCvApplicationDTO.getCvId());
		if (!cv.getPerson().getUser().getId().equals(user.getId())) {
			throw new CvNotOwnedException(localizedMessageService.getMessage("cv.not_owned"));
		}

		if(cv.equals(application.getCv())) {
			throw new AlreadyAssignedCvException(localizedMessageService.getMessage("application.cv.already_owned"));
		}

		application.setCv(cv);
		application.setUpdateDate(LocalDateTime.now());
		application.setApplicationState(ApplicationState.MODIFIED);

		application = applicationRepository.save(application);
		return applicationMapper.mapToResponseApplicationDTO(application);
	}

	private boolean existsApplicationInVacancy(Long vacancyId, Long personId) {
		List<Application> applicationList = applicationRepository.findAllByVacancy_IdAndPerson_Id(vacancyId, personId);
		return applicationList.isEmpty();
	}

	private boolean validateComments(String comments) {
		String sanitizedComment = Encode.forHtml(comments);

		String regex = "^[\\p{L}0-9.,!?\\s]+$";

		for (String pattern : dangerousPatterns) {
			if (sanitizedComment.toLowerCase().contains(pattern.toLowerCase())) {
				return false;
			}
		}

		return sanitizedComment.matches(regex);
	}

	private Application createApplicationEntity(RequestApplicationDTO requestApplicationDTO, Person person) {
		Application application = new Application();
		application.setPerson(person);
		application.setVacancy(vacancyService.findById(requestApplicationDTO.getVacancyId()));
		application.setComments(requestApplicationDTO.getComments());
		application.setApplicationDate(LocalDateTime.now());
		application.setUpdateDate(LocalDateTime.now());
		application.setApplicationState(ApplicationState.IN_PROCESS);
		return application;
	}

	private List<DetailSkill> mapDetailSkills(RequestApplicationDTO requestApplicationDTO, Application application) {
		return requestApplicationDTO.getRequestDetailSkillDTOS().stream().map(skillDto -> {
			DetailSkill detailSkill = new DetailSkill();
			detailSkill.setApplication(application);
			detailSkill.setVacancy(vacancyService.findById(skillDto.getVacancyId()));
			detailSkill.setSkill(skillDto.getSkillId() != null ? skillService.findById(skillDto.getSkillId()) : null);
			detailSkill.setLanguage(skillDto.getLanguageId() != null ? languageService.findById(skillDto.getLanguageId()) : null);
			detailSkill.setIsObligatory(skillDto.getIsObligatory());
			detailSkill.setPriority(skillDto.getPriority());
			detailSkill.setYearsExperience(skillDto.getYearsExperience());
			return detailSkill;
		}).toList();
	}

}
