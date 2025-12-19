package com.techforb.apiportalrecruiting.core.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VacancyRequestUpdateDTO {
    @Schema(description = "Nombre del puesto de la vacante", example = "Backend Developer", required = true)
    @NotBlank(message = "Role is mandatory")
    private String role;
    @Schema(description = "Descripcion de la vacante", example = "We are looking for a Java developer", required = true)
    @NotBlank(message = "Description is mandatory")
    private String description;
    @Schema(description = "Nivel requerido del idioma", example = "B2", required = true)
    @NotBlank
    private String languageLevel;
    @Schema(description = "Años de experiencia requerido para la vacante", example = "3", required = true)
    @NotNull(message = "YearsExperienced is mandatory")
    private Integer yearsExperienceRequired;
}
