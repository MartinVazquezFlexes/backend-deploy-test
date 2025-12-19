package com.techforb.apiportalrecruiting.modules.backoffice.user.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GoogleLoginRequestDTO {
    @Schema(description = "El token de acceso obtenido de Google para autenticar al usuario", example = "ya29.a0ARrdaM8d...")
    @NotBlank(message = "El accessToken es obligatorio")
    private String accessToken;

}
