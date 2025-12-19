package com.techforb.apiportalrecruiting.modules.portal.savedvacancies.controller;

import com.techforb.apiportalrecruiting.modules.portal.savedvacancies.dto.SavedVacancyDTO;
import com.techforb.apiportalrecruiting.modules.portal.savedvacancies.service.SavedVacancyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/portal/saved-jobs")
@RequiredArgsConstructor
@Tag(name = "Saved Jobs", description = "Endpoints para gestionar trabajos guardados por usuarios")
public class SavedVacancyController {

    private final SavedVacancyService savedVacancyService;

    @Operation(
            summary = "Obtener trabajos guardados",
            description = "Retorna todas las vacantes que el usuario autenticado ha guardado como favoritas. " +
                         "Los resultados están paginados y ordenados por fecha de guardado (más recientes primero)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "Lista de trabajos guardados obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(
                    responseCode = "401", 
                    description = "Usuario no autenticado - falta token Bearer o token inválido",
                    content = @Content
            )
    })
    @GetMapping
    public ResponseEntity<Page<SavedVacancyDTO>> getSavedJobs(
            @Parameter(description = "Parámetros de paginación y ordenamiento")
            @PageableDefault(page = 0, size = 10, sort = {"savedDate"}, 
                           direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<SavedVacancyDTO> savedJobs = savedVacancyService.getSavedVacancies(pageable);
        return ResponseEntity.ok(savedJobs);
    }

    @Operation(
            summary = "Guardar trabajo",
            description = "Permite al usuario autenticado guardar una vacante como favorita sin notas adicionales"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", 
                    description = "Trabajo guardado exitosamente",
                    content = @Content(schema = @Schema(implementation = SavedVacancyDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400", 
                    description = "Vacante inactiva - no se puede guardar una vacante que no está activa",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401", 
                    description = "Usuario no autenticado - falta token Bearer o token inválido",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404", 
                    description = "Vacante no encontrada - el ID de vacante especificado no existe",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409", 
                    description = "Conflicto - la vacante ya fue guardada por este usuario",
                    content = @Content
            )
    })
    @PostMapping("/{vacancyId}")
    public ResponseEntity<SavedVacancyDTO> saveJob(
            @Parameter(description = "ID de la vacante a guardar")
            @PathVariable Long vacancyId) {
        
        SavedVacancyDTO savedJob = savedVacancyService.saveVacancy(vacancyId);
        return ResponseEntity.status(201).body(savedJob);
    }


} 