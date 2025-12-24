package com.techforb.apiportalrecruiting.modules.backoffice.user.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmailLoginRequestDTO {
    @Schema(description = "El email del usuario", example = "usuario@dominio.com")
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email must be valid")
    private String email;
    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password must have at least 8 characters")
    @Schema(description = "La contraseña del usuario", example = "contraseña123")
    private String password;

}
