package com.techforb.apiportalrecruiting.modules.portal.applications.controllers;

import com.techforb.apiportalrecruiting.core.entities.Person;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.cv.CvWithCreationDateDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.cv.ResponsePagCvDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.CvService;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.PersonService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/cv")
@RequiredArgsConstructor
public class CvController {

	private final CvService cvService;
	private final PersonService personService;

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
	public ResponseEntity<String> saveCv(
			@Parameter(description = "ID of the person", required = true)
			@PathVariable Long idPerson,
			@Parameter(description = "Flag indicating if the upload is from the profile page", required = true)
			@RequestPart("fromProfile") Boolean fromProfile,
			@Parameter(description = "CV file to upload (multipart/form-data)", required = true)
			@RequestPart("cv") MultipartFile cvFile){
		Person person = personService.getPersonById(idPerson);
		cvService.uploadCv(cvFile, person, "", fromProfile);
		return ResponseEntity.status(201).body("Cv upload successful");
	}

	@DeleteMapping("/delete")
	public ResponseEntity<Boolean> deleteCv(@RequestParam Long personId, @RequestParam Long cvId) throws IOException  {
		return ResponseEntity.ok(this.cvService.deleteCvByIdAndPersonId(cvId, personId));
	}

}
