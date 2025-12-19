package com.techforb.apiportalrecruiting.modules.portal.applications.services.impl.cv;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.entities.Application;
import com.techforb.apiportalrecruiting.core.entities.Cv;
import com.techforb.apiportalrecruiting.core.entities.Person;
import com.techforb.apiportalrecruiting.core.entities.Skill;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.core.exceptions.UnauthorizedActionException;
import com.techforb.apiportalrecruiting.core.security.cloudinary.CloudinaryService;
import com.techforb.apiportalrecruiting.modules.backoffice.user.UserService;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.ApplicationModified;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.CvDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.CvWithCreationDateDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.cv.ResponsePagCvDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.ApplicationRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.CvRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.CvService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CvServiceImpl implements CvService {

	private final CvRepository cvRepository;
	private final LocalizedMessageService localizedMessageService;
	private final CloudinaryService cloudinaryService;
	private final UserService userService;
	private final ApplicationRepository applicationRepository;

	@Override
	public Cv uploadCv(MultipartFile cvFile, Person person, String paramFolder, Boolean fromProfile) {
		if (cvFile.isEmpty() || !cvFile.getContentType().equals(MediaType.APPLICATION_PDF_VALUE)) {
			throw new RuntimeException(localizedMessageService.getMessage("cv.pdf_file"));
		}

		if (cvFile.getSize() > 2 * 1024 * 1024) {
			throw new RuntimeException(localizedMessageService.getMessage("cv.size"));
		}

		Map uploadResult = cloudinaryService.uploadCv(cvFile, paramFolder);

		if (uploadResult == null) {
			throw new RuntimeException(localizedMessageService.getMessage("cv.not_be_null"));
		}
		String publicId = (String) uploadResult.get("public_id");
		String version = uploadResult.get("version").toString();

		if(fromProfile){
			Optional<Cv> dbCv = cvRepository.findByPersonIdAndIsLastTrue(person.getId());
			if(dbCv.isPresent()){
				Cv cv = dbCv.get();
				cv.setIsLast(false);
				cvRepository.save(cv);
			}
		}

		Cv cv = new Cv();
		cv.setPublicId(publicId);
		cv.setVersion(version);
		cv.setPerson(person);
		cv.setIsLast(fromProfile);
		cv.setName(person.getFirstName() + "_" + person.getLastName());
		return cvRepository.save(cv);
	}

	@Override
	public Cv findCvById(Long id) {
		return cvRepository.findById(id).orElseThrow(()->new EntityNotFoundException(localizedMessageService.getMessage("cv.not_found")));
	}

	@Override
	public Page<ResponsePagCvDTO> getFilteredCvs(String country, String skill, Pageable pageable) {
		UserEntity user = userService.getUserFromContext();
		if (user.getRoles().stream().noneMatch(r -> r.getName().equals("RECRUITER"))) {

			throw new UnauthorizedActionException(localizedMessageService.getMessage("user.without_permissions"));
		}

		Specification<Cv> spec = Specification.where(CvSpecification.hasCountryLike(country))
				.and(CvSpecification.hasSkill(skill));

		return cvRepository.findAll(spec, pageable).map(this::convertToDto);
	}

	@Override
	public Page<CvWithCreationDateDTO> getCvsById(Long idPerson, Boolean isLast, Pageable pageable) {

		UserEntity user = userService.getUserFromContext();
		if(!user.getPerson().getId().equals(idPerson)){
			throw new UnauthorizedActionException(localizedMessageService.getMessage("user.without_permissions"));
		}
		Specification<Cv> spec = Specification.where(CvSpecification.hasIsLast(isLast)).and(CvSpecification.hasIdPerson(idPerson));
		return cvRepository.findAll(spec,pageable).map(this::convertToDtoForMyCvs);
	}
	private CvWithCreationDateDTO convertToDtoForMyCvs(Cv cv){
		String signedUrl = cloudinaryService.generateSignedUrl(cv.getPublicId(), cv.getVersion());
		return new CvWithCreationDateDTO(cv.getId(),signedUrl,cv.getName(),cv.getCreationDate(),cv.getIsLast());
	}
	private ResponsePagCvDTO convertToDto(Cv cv) {

		String signedUrl = cloudinaryService.generateSignedUrl(cv.getPublicId(), cv.getVersion());
		return new ResponsePagCvDTO(
				signedUrl,
				cv.getName(),
				cv.getPerson().getUser().getEmail(),
				cv.getPerson().getDirection() != null ? cv.getPerson().getDirection().getCity().getProvince().getCountry().getName(): null,
				cv.getPerson().getSkills().stream().map(Skill::getDescription).collect(Collectors.toList())
		);
	}


	@Override
	@Transactional
	public boolean deleteCvByIdAndPersonId(Long cvId, Long personId) throws IOException {
		Cv cv = this.findCvById(cvId);

		if (!cv.getPerson().getId().equals(personId)) {
			throw new AccessDeniedException(
					localizedMessageService.getMessage("cv.unauthorized_deletion"));
		}

		if (cv.getIsLast()) {
			List<Cv> allCvsPerPersonId = cvRepository.findAllByPersonIdOrderByIdDesc(personId);
			Cv nextLatestCv = allCvsPerPersonId.stream()
					.filter(c -> !c.getId().equals(cvId))
					.findFirst()
					.orElse(null);
			if (nextLatestCv != null) {
				nextLatestCv.setIsLast(true);
				cvRepository.save(nextLatestCv);
			}
		}

		List<Application> applications = applicationRepository.findByCvId(cv.getId());
		if (!applications.isEmpty()) {
			for (Application application : applications) {
				application.setCv(null);
				applicationRepository.save(application);
			}
		}

		if (cv.getPublicId() != null) {
			this.cloudinaryService.deleteAuthenticatedFile(cv.getPublicId());
		}

		this.cvRepository.delete(cv);
		return true;
	}
}
