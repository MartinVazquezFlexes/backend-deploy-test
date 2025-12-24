package com.techforb.apiportalrecruiting.modules.portal.applications.controllers;

import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.LanguageDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/languages")
@Tag(name = "Controlador lenguajes ", description = "Endpoint para gestionar el lenguaje personal del usuarios")
public class LanguageController {

    private final LanguageService languageService;

    @Operation(
            summary = "Obtiene todos los nieveles del lenguaje ingles",
            description = "Devuelve una lista de todos los niveles disponibles en el sistema para el lenguaje ingles."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Niveles de lenguaje obtenidos exitosamente"
            )
    }
    )
    @GetMapping("/all-english-levels")
    public ResponseEntity<List<LanguageDTO>> getAll() {
        List<LanguageDTO> languages = languageService.listEnglishLevels();
        return ResponseEntity.ok(languages);
    }
}
