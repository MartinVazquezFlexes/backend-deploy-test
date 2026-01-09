package com.techforb.apiportalrecruiting.modules.portal.applications.controllers;

import com.techforb.apiportalrecruiting.core.entities.Cv;
import com.techforb.apiportalrecruiting.core.entities.Person;
import com.techforb.apiportalrecruiting.core.security.cloudinary.CloudinaryService;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.cv.CvWithCreationDateDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.cv.ResponsePagCvDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.CvRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.CvService;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.PersonService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/cv")
@RequiredArgsConstructor
public class CvController {

	private final CvService cvService;
	private final PersonService personService;
	private final CloudinaryService cloudinaryService;
	private final CvRepository cvRepository;
	private static final Logger log = LoggerFactory.getLogger(CvController.class);


	@GetMapping("/get-cvs-filtered")
	public ResponseEntity<Page<ResponsePagCvDTO>> getFilteredCvs(
			@RequestParam(required = false) String country,
			@RequestParam(required = false) String skill,
			@PageableDefault(page = 0, size = 10, sort = {"person.user.email"},
					direction = Sort.Direction.ASC) Pageable pageable) {
		return ResponseEntity.ok(cvService.getFilteredCvs(country, skill, pageable));
	}

	@GetMapping("/get-myCvs/{idPerson}")
	public ResponseEntity<Page<CvWithCreationDateDTO>> getCvsById(
			@PathVariable Long idPerson,
			@RequestParam(required = false) Boolean isLast,
			@PageableDefault(page = 0, size = 10, sort = {"creationDate"},
					direction = Sort.Direction.DESC) Pageable pageable)
	{
		return ResponseEntity.ok(this.cvService.getCvsById(idPerson, isLast, pageable));
  	}

	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "CV uploaded successfully"),
			@ApiResponse(responseCode = "404", description = "Person not found", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid input or file missing", content = @Content)
	})
	@PostMapping("/upload/{idPerson}")
	public ResponseEntity<Map<String, String>> saveCv(
			@Parameter(description = "ID of the person", required = true)
			@PathVariable Long idPerson,

			@Parameter(description = "Flag indicating if the upload is from the profile page", required = true)
			@RequestParam("fromProfile") Boolean fromProfile,

			@Parameter(description = "CV file to upload (multipart/form-data)", required = true)
			@RequestPart("cv") MultipartFile cvFile
	) {
		Person person = personService.getPersonById(idPerson);
		cvService.uploadCv(cvFile, person, "", fromProfile);

		return ResponseEntity.status(201)
				.body(Map.of("message", "Cv upload successful"));
	}


	@DeleteMapping("/delete")
	public ResponseEntity<Boolean> deleteCv(@RequestParam Long personId, @RequestParam Long cvId) throws IOException  {
		return ResponseEntity.ok(this.cvService.deleteCvByIdAndPersonId(cvId, personId));
	}

	// En tu CvController //TODO NUEVO
	@GetMapping("/view/{cvId}")
	public ResponseEntity<byte[]> viewCv(
			@PathVariable Long cvId,
			@RequestParam Long personId) {

		try {
			log.info("Intentando ver CV - cvId: {}, personId: {}", cvId, personId);

			// Obtener el CV de la base de datos
			Optional<Cv> cvOptional = cvRepository.findById(cvId);

			if (cvOptional.isEmpty()) {
				log.error("CV no encontrado con ID: {}", cvId);
				return ResponseEntity.notFound().build();
			}

			Cv cv = cvOptional.get();

			// Verificar que pertenece a la persona
			if (!cv.getPerson().getId().equals(personId)) {
				log.error("CV {} no pertenece a la persona {}", cvId, personId);
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}

			log.info("CV encontrado - publicId: {}, name: {}", cv.getPublicId(), cv.getName());

			// Descargar el archivo desde Cloudinary
			byte[] fileContent = cloudinaryService.downloadFile(cv.getPublicId());

			if (fileContent == null || fileContent.length == 0) {
				log.error("El contenido del archivo está vacío");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}

			log.info("Archivo descargado exitosamente - tamaño: {} bytes", fileContent.length);

			// Determinar el content type
			String contentType = "application/pdf";
			if (cv.getName() != null) {
				if (cv.getName().toLowerCase().endsWith(".doc")) {
					contentType = "application/msword";
				} else if (cv.getName().toLowerCase().endsWith(".docx")) {
					contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
				}
			}

			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + cv.getName() + "\"")
					.contentType(MediaType.parseMediaType(contentType))
					.body(fileContent);

		} catch (Exception e) {
			log.error("Error al ver CV - cvId: {}, personId: {}", cvId, personId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

}
