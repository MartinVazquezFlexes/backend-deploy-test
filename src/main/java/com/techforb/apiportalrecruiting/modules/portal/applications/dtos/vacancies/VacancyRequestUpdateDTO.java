package com.techforb.apiportalrecruiting.modules.portal.applications.dtos.vacancies;

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

        @Schema(description = "Nombre del puesto de la vacante",
                example = "Backend Developer")
        @NotBlank(message = "Role is mandatory")
        private String role;

        @Schema(description = "Descripcion de la vacante",
                example = "We are looking for a Java developer")
        @NotBlank(message = "Description is mandatory")
        private String description;

        @Schema(description = "Nivel requerido del idioma",
                example = "B2")
        @NotBlank
        private String languageLevel;

        @Schema(description = "Años de experiencia requerido para la vacante",
                example = "3")
        @NotNull
        private Integer yearsExperienceRequired;

}
