package com.techforb.apiportalrecruiting.core.dtos.users;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserLoginResponseDTO {
    @Schema(description = "El id del usuario")
    private Long id;
    @Schema(description = "El email del usuario")
    private String email;
    @Schema(description = "URL de la imagen de perfil del proveedor (puede ser null)")
    private String profileImageUrl;
}
