package com.techforb.apiportalrecruiting.modules.portal.applications.controllers;

import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.SkillItemDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.SkillService;
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
@RequestMapping("api/skills")
@Tag(name = "Controlador de Skills", description = "Endpoint para gestionar las habilidades de los usuarios")
public class SkillController {

    private final SkillService skillService;

    @Operation(
            summary = "Obtiene todas las skills disponibles",
            description = "Devuelve una lista de todas las habilidades disponibles en el sistema ordenadas alfabéticamente."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Skills obtenidas exitosamente"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    }
    )
    @GetMapping("/all")
    public ResponseEntity<List<SkillItemDTO>> getAll() {
        List<SkillItemDTO> skills = skillService.getAll();
        return ResponseEntity.ok(skills);
    }
}
