package com.techforb.apiportalrecruiting.modules.backoffice.user.dtos;


import com.techforb.apiportalrecruiting.core.security.jwt.Jwt;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginResponseDTO {
    @Schema(description = "El usuario autenticado, representado por un dto de usuario", implementation = UserLoginResponseDTO.class)
    private UserLoginResponseDTO user;
    @Schema(description = "El JWT generado para el usuario tras la autenticación", implementation = Jwt.class)
    private Jwt jwt;
}
