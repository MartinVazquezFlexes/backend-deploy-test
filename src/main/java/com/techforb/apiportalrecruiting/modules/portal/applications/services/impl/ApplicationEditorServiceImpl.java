package com.techforb.apiportalrecruiting.modules.portal.applications.services.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.entities.Application;
import com.techforb.apiportalrecruiting.core.entities.ApplicationState;
import com.techforb.apiportalrecruiting.core.entities.Cv;
import com.techforb.apiportalrecruiting.core.entities.Language;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application.ApplicationDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application.ApplicationMapper;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application.ApplicationModified;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.DetailSkillUpdateDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.ApplicationRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.CvRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.LanguageRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.ApplicationEditorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationEditorServiceImpl implements ApplicationEditorService {

    private final ApplicationRepository applicationRepository;
    private final CvRepository cvRepository;
    private final LanguageRepository languageRepository;
    private final ApplicationMapper applicationMapper;
    private final LocalizedMessageService localizedMessageService;


    @Override
    public List<ApplicationDTO> getApplicationByApplicantId(Long id) {
        List<Application> applications = applicationRepository.findByPersonId(id);
        return applications.stream()
                .map(applicationMapper::mapToDTO)
                .toList();  // Compliant: Produces an unmodifiable list directly
    }

    @Override
    public ApplicationDTO modifyApplication(Long id, ApplicationModified applicationModified) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(localizedMessageService.getMessage("application.not_found")));

        if (application.getApplicationState() == ApplicationState.CANCELED ||
                application.getApplicationState() == ApplicationState.FINISHED) {
            throw new IllegalStateException(localizedMessageService.getMessage("application.modify.invalid_state"));
        }

        if (applicationModified.getApplicationState() == ApplicationState.CANCELED) {
            throw new IllegalStateException(localizedMessageService.getMessage("application.manual_cancel_forbidden"));
        }

        if (applicationModified.getComments() != null) {
            application.setComments(applicationModified.getComments());
        }

        if (applicationModified.getApplicationState() != null) {
            application.setApplicationState(applicationModified.getApplicationState());
        }

        if (applicationModified.getCvId() != null) {
            Cv cv = cvRepository.findById(applicationModified.getCvId())
                    .orElseThrow(() -> new EntityNotFoundException(localizedMessageService.getMessage("cv.not_found")));
            application.setCv(cv);
        }

        if (applicationModified.getDetailSkills() != null && !applicationModified.getDetailSkills().isEmpty()) {
            Map<Long, Integer> skillUpdates = applicationModified.getDetailSkills().stream()
                    .collect(Collectors.toMap(DetailSkillUpdateDTO::getId, DetailSkillUpdateDTO::getYearsExperience));

            application.getDetailSkills().forEach(detailSkill -> {
                if (skillUpdates.containsKey(detailSkill.getId())) {
                    detailSkill.setYearsExperience(skillUpdates.get(detailSkill.getId()));
                }
            });
        }

        if (applicationModified.getLanguageId() != null) {
            Language language = languageRepository.findById(applicationModified.getLanguageId())
                    .orElseThrow(() -> new EntityNotFoundException(localizedMessageService.getMessage("language.not_found")));

            application.getDetailSkills().stream()
                    .filter(detailSkill -> detailSkill.getLanguage() != null)
                    .forEach(detailSkill -> detailSkill.setLanguage(language));
        }

        applicationRepository.save(application);
        return applicationMapper.mapToDTO(application);
    }

    @Override
    public ApplicationDTO modifyStateApplication(Long id, ApplicationState state) {

        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(localizedMessageService.getMessage("application.not_found")));
        if (application.getApplicationState() == ApplicationState.CANCELED ||
                application.getApplicationState() == ApplicationState.FINISHED) {
            throw new IllegalStateException(localizedMessageService.getMessage("application.modify.invalid_state"));
        }
        application.setApplicationState(state);
        applicationRepository.save(application);
        return applicationMapper.mapToDTO(application);
    }
}
