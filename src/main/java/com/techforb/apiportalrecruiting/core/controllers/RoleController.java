package com.techforb.apiportalrecruiting.core.controllers;

import com.techforb.apiportalrecruiting.core.services.RoleService;
import com.techforb.apiportalrecruiting.core.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/role")
@RequiredArgsConstructor
@Tag(name = "Role controller", description = "Endpoints para gestionar roles de usuarios")
public class RoleController {

    private final RoleService roleService;
    private final UserService userService;


    @Operation(
            summary = "Permite la autoasignacion de rol para los reclutas o postulante",
            description = "Permite al usuario elegir ser recluta o postulante en la plataforma"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cambio realizado con exito"
            )
    }
    )
    @PostMapping("/self-assign")
    @PreAuthorize("hasRole('DEFAULT')") 
    public ResponseEntity<String> selfAssignRole(@RequestParam String roleName) {
        String userEmail = userService.getUserFromContext().getEmail();
        roleService.assignRoleToUser(roleName, userEmail);
        return ResponseEntity.ok().body("Rol asignado exitosamente");
    }

}
