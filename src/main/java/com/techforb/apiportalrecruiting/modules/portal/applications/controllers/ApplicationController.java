package com.techforb.apiportalrecruiting.modules.portal.applications.controllers;

import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application.RequestApplicationDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application.RequestChangeCvApplicationDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.application.ResponseApplicationDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/portal/applications")
@RequiredArgsConstructor
public class ApplicationController {

	private final ApplicationService applicationService;

	@Operation(summary = "Aplicar a una vacante")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Aplicado a la postulación exitosamente"),
			@ApiResponse(responseCode = "400", description = "No se pudo añadir a la postulación")
	})
	@PostMapping("/apply-vacancy")
	public ResponseEntity<ResponseApplicationDTO> apply(@RequestPart("application") RequestApplicationDTO requestApplicationDTO,
														@RequestPart("cv") MultipartFile cvFile) throws IOException {
		return ResponseEntity.ok(applicationService.applyVacancy(requestApplicationDTO, cvFile));
	}

	@Operation(summary = "Modificar el cv en una postulación de una vacante")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Postulación modificada exitosamente"),
			@ApiResponse(responseCode = "400", description = "No se pudo modificar el cv de la postulación")
	})
	@PutMapping("/modify-application-vacancy")
	public ResponseEntity<ResponseApplicationDTO> changeCvApplication(@RequestBody RequestChangeCvApplicationDTO requestApplication) {
		return ResponseEntity.ok(applicationService.changeCvApplication(requestApplication));
	}
}
