package com.techforb.apiportalrecruiting.core.controllers;

import com.techforb.apiportalrecruiting.core.entities.RoleFunctional;
import com.techforb.apiportalrecruiting.core.services.RoleFunctionalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/role-functional")
@Tag(name = "Functional role controller ", description = "Endpoints para gestionar roles funcionales del usuarios")
public class RoleFunctionalController {

    private final RoleFunctionalService roleFunctionalService;
    @Operation(
            summary = "Obtiene todos los roles funcionales",
            description = "Devuelve una lista de todos los roles funcionales disponibles en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de roles funcionales obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RoleFunctional.class)
                    )
            )
    }
    )
    @GetMapping("/all")
    @PreAuthorize("hasRole('APPLICANT')")
    public ResponseEntity<List<RoleFunctional>> getAllRolesFunctional() {
        List<RoleFunctional> roles = roleFunctionalService.getAllRolesFunctional();
        return ResponseEntity.ok(roles);
    }
}
