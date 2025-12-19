package com.techforb.apiportalrecruiting.modules.portal.direction.controller;

import com.techforb.apiportalrecruiting.modules.portal.direction.dto.CountryItemDTO;
import com.techforb.apiportalrecruiting.modules.portal.direction.service.CountryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/portal/direction/countries")
@Tag(name = "Countries", description = "Operaciones para listar países")
@RequiredArgsConstructor
public class CountryController {

	private final CountryService countryService;

	@GetMapping
	@Operation(summary = "Listar países", description = "Obtiene el listado de países ordenado por nombre")
	@ApiResponse(responseCode = "200", description = "Listado obtenido",
		content = @Content(array = @ArraySchema(schema = @Schema(implementation = CountryItemDTO.class))))
	public ResponseEntity<List<CountryItemDTO>> list() {
		return ResponseEntity.ok(countryService.listAll());
	}
}


